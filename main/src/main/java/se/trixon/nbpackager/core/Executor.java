/*
 * Copyright 2024 Patrik Karlström <patrik@trixon.se>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package se.trixon.nbpackager.core;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import net.lingala.zip4j.ZipFile;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.MessageDigestAlgorithms;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.netbeans.api.progress.ProgressHandle;
import org.openide.awt.StatusDisplayer;
import org.openide.util.Cancellable;
import org.openide.windows.FoldHandle;
import org.openide.windows.IOFolding;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;
import se.trixon.almond.nbp.output.OutputHelper;
import se.trixon.almond.nbp.output.OutputLineMode;
import se.trixon.almond.util.Dict;
import se.trixon.nbpackager.Options;
import static se.trixon.nbpackager.Options.*;

/**
 *
 * @author Patrik Karlström <patrik@trixon.se>
 */
public class Executor implements Runnable {

    private String mContentDir = "NOT_AVAILABLE_IN_DRY_RUN";
    private Process mCurrentProcess;
    private File mDestDir;
    private final boolean mDryRun;
    private String mDryRunIndicator = "";
    private Thread mExecutorThread;
    private final InputOutput mInputOutput;
    private boolean mInterrupted;
    private File mLinuxTargetFile;
    private FoldHandle mMainFoldHandle;
    private final Options mOptions = Options.getInstance();
    private final OutputHelper mOutputHelper;
    private ProgressHandle mProgressHandle;
    private final StatusDisplayer mStatusDisplayer = StatusDisplayer.getDefault();
    private final Task mTask;
    private File mTempDir;
    private final String mVersion;

    public Executor(Task task, boolean dryRun) {
        mTask = task;
        mDryRun = dryRun;
        mInputOutput = IOProvider.getDefault().getIO(mTask.getName(), false);
        mInputOutput.select();

        if (mDryRun) {
            mDryRunIndicator = String.format(" (%s)", Dict.DRY_RUN.toString());
        }

        mOutputHelper = new OutputHelper(mTask.getName(), mInputOutput, mDryRun);
        mOutputHelper.reset();
        mVersion = StringUtils.substringAfter(mTask.getBasename(), "-");
    }

    @Override
    public void run() {
        var allowToCancel = (Cancellable) () -> {
            mExecutorThread.interrupt();
            mInterrupted = true;
            mProgressHandle.finish();
            ExecutorManager.getInstance().getExecutors().remove(mTask.getId());
            jobEnded(OutputLineMode.WARNING, Dict.CANCELED.toString());

            return true;
        };

        mProgressHandle = ProgressHandle.createHandle(mTask.getName(), allowToCancel);
        mProgressHandle.start();
        mProgressHandle.switchToIndeterminate();

        mExecutorThread = new Thread(() -> {
            mOutputHelper.start();
            mOutputHelper.printSectionHeader(OutputLineMode.INFO, Dict.START.toString(), Dict.TASK.toLower(), mTask.getName());
            mMainFoldHandle = IOFolding.startFold(mInputOutput, true);

            if (!mTask.isValid()) {
                mInputOutput.getErr().println(mTask.getValidationError());
                jobEnded(OutputLineMode.ERROR, Dict.INVALID_INPUT.toString());
                mInputOutput.getErr().println(String.format("\n\n%s", Dict.JOB_FAILED.toString()));

                return;
            }

            try {
                mDestDir = new File(mTask.getDestDir(), FilenameUtils.getBaseName(mTask.getSourceFile().getName()));

                if (!mDryRun) {
                    if (!initTargetDirectory()) {
                        mInputOutput.getErr().println("\nOperation cancelled");
                        return;
                    }
                }

                if (mTask.getScriptPre() != null) {
                    mInputOutput.getOut().println("Run PRE execution script");
                    executeScript(null, null, mTask.getScriptPre());
                }

                if (!mInterrupted) {
                    unzip();
                }

                if (!mInterrupted && mTask.isTargetAny()) {
                    createPackage("any");
                }

                if (!mInterrupted && mTask.isTargetLinux()) {
                    createPackage("linux");

                    if (!mInterrupted && mTask.isTargetLinuxAppImage()) {
                        createPackageAppImage();
                    }

                    if (!mInterrupted && mTask.isTargetLinuxSnap()) {
                        createPackageSnap();
                    }
                }

                if (!mInterrupted && mTask.isTargetMac()) {
                    createPackage("mac");
                }

                if (!mInterrupted && mTask.isTargetWindows()) {
                    createPackage("windows");
                }

                if (!mInterrupted && mTask.getScriptPost() != null) {
                    mInputOutput.getOut().println("Run POST execution script");
                    executeScript(null, null, mTask.getScriptPost());
                }

                FileUtils.deleteDirectory(mTempDir);

                if (mInterrupted) {
                    mInputOutput.getErr().println("\nOperation interrupted");
                } else {
                    mInputOutput.getOut().println("\nOperation completed" + (mDryRun ? " (dry-run)" : ""));
                }
            } catch (IOException e) {
//TODO
            }

            if (!mInterrupted) {
                jobEnded(OutputLineMode.OK, Dict.DONE.toString());

                if (!mDryRun) {
                    mTask.setLastRun(System.currentTimeMillis());
                    StorageManager.save();
                }
            }

            mProgressHandle.finish();
            ExecutorManager.getInstance().getExecutors().remove(mTask.getId());
        }, "Executor");

        mExecutorThread.start();
    }

    private void copyJre(File jreDir, File targetDir) throws IOException {
        if (jreDir == null) {
            mInputOutput.getOut().println("No jre specified.");
            return;
        }
        var etc = String.format("etc/%s.conf", mContentDir);
        var jreName = jreDir.getName();
        var destDir = new File(targetDir, jreName);
        var etcFile = new File(targetDir, etc);
        mInputOutput.getOut().println("set jdkhome in " + etcFile.getAbsolutePath());
        mInputOutput.getOut().println("copy jre to: " + destDir.getAbsolutePath());
        if (!mDryRun) {
            var etcContent = FileUtils.readFileToString(etcFile, "utf-8");
            var key = StringUtils.contains(etcContent, "netbeans_jdkhome") ? "netbeans_jdkhome" : "jdkhome";
            FileUtils.write(etcFile, String.format("\n\n# Added by Packager\n%s=\"%s\"\n", key, jreName), "utf-8", true);
            cp(jreDir, destDir, false);
        }
    }

    private void cp(File source, File dest, boolean contentOnly) {
        var sourcePath = source.getAbsolutePath();
        if (contentOnly && source.isDirectory()) {
            sourcePath += "/.";
        }
        execute(null, null, "cp", "-ra", sourcePath, dest.getAbsolutePath());
        //FileUtils.copyDirectory(source, dest, true);
    }

    private void createChecksum(File file, String algorithm) throws IOException {
        var digestFile = new File(file.getAbsolutePath() + String.format(".%s", StringUtils.remove(algorithm, "-").toLowerCase(Locale.getDefault())));
        mInputOutput.getOut().println("create checksum: " + digestFile.getAbsolutePath());
        if (!mDryRun) {
            var digest = new DigestUtils(algorithm).digestAsHex(file);
            FileUtils.writeStringToFile(digestFile, String.format("%s  %s", digest, file.getName()), Charset.defaultCharset());
        }
    }

    private void createChecksums(File file) throws IOException {
        if (mTask.isChecksumSha256()) {
            createChecksum(file, MessageDigestAlgorithms.SHA_256);
        }

        if (mTask.isChecksumSha512()) {
            createChecksum(file, MessageDigestAlgorithms.SHA_512);
        }
    }

    private void createPackage(String target) throws IOException {
        mInputOutput.getOut().println("\ncreate package: " + target);

        var targetDir = new File(mDestDir, target);

        mInputOutput.getOut().println("copy zip contents to: " + targetDir.getAbsolutePath());
        if (!mDryRun) {
            targetDir.mkdirs();
            cp(mTempDir, targetDir, true);
        }

        var baseDir = mTask.getResourceDir();
        targetDir = new File(targetDir, mContentDir);
        if (baseDir != null) {
            mInputOutput.getOut().println("copy resources to: " + targetDir.getAbsolutePath());
            if (!mDryRun) {
                cp(new File(baseDir, "any"), targetDir, true);
                if (!"any".equals(target)) {
                    cp(new File(baseDir, target), targetDir, true);
                }
            }
        }

        boolean keepWindows = false;
        if (target.equalsIgnoreCase("linux") && mTask.isTargetLinux()) {
            copyJre(mTask.getJreLinux(), targetDir);
        } else if (target.equalsIgnoreCase("mac") && mTask.isTargetMac()) {
            copyJre(mTask.getJreMac(), targetDir);
        } else if (target.equalsIgnoreCase("windows") && mTask.isTargetWindows()) {
            copyJre(mTask.getJreWindows(), targetDir);
            keepWindows = true;
        }

        if (!target.equalsIgnoreCase("any")) {
            removeBin(new File(targetDir, "bin"), keepWindows);
        }

        var targetFile = new File(mDestDir, String.format("%s-%s.zip", mTask.getBasename(), target));
        var contentDir = mContentDir;
        if (target.equals("linux")) {
            mLinuxTargetFile = targetFile;
        } else if (target.equals("mac")) {
            var oldTargetDir = targetDir;
            targetDir = new File(targetDir.getPath() + ".app");
            FileUtils.moveDirectory(oldTargetDir, targetDir);
            contentDir += ".app";
        }

        mInputOutput.getOut().println("creating zip: " + targetFile.getAbsolutePath());
        execute(null, targetDir.getParentFile(), "zip", "-qr", targetFile.getAbsolutePath(), contentDir);

        createChecksums(targetFile);
    }

    private void createPackageAppImage() throws IOException {
        mInputOutput.getOut().println("\ncreate package: AppImage");
        mInputOutput.getOut().println("copy template to: " + mDestDir.getAbsolutePath());
        var templateName = mTask.getTemplateDirAppImage().getName();
        templateName = StringUtils.replace(templateName, "__", String.format("-%s-", mVersion));
        var targetDir = new File(mDestDir, templateName);
        var targetFile = new File(mDestDir, StringUtils.replace(templateName, "AppDir", "AppImage"));

        if (!mDryRun) {
            cp(mTask.getTemplateDirAppImage(), targetDir, false);
        }

        var usrDir = new File(targetDir, "usr");
        mInputOutput.getOut().println("copy zip contents to: " + usrDir.getAbsolutePath());
        if (!mDryRun) {
            cp(new File(mTempDir, mContentDir), usrDir, true);
        }

        removeBin(new File(usrDir, "bin"), false);
        copyJre(mTask.getJreLinux(), usrDir);

        var environment = new HashMap<String, String>();
//        environment.put("ARCH", "x86_64");
        var command = new ArrayList<String>();
        command.add(mOptions.get(OPT_APP_IMAGE_TOOL, "NO_COMMAND_SPECIFIED_CHECK_YOUR_SETTINGS"));
        for (var option : StringUtils.split(mOptions.get(OPT_APP_IMAGE_OPTIONS, ""))) {
            command.add(option);
        }

        command.add(targetDir.getAbsolutePath());
        command.add(targetFile.getAbsolutePath());
        execute(command, environment, null);

        createChecksums(targetFile);
    }

    private void createPackageSnap() throws IOException {
        mInputOutput.getOut().println("\ncreate package: Snap");
        mInputOutput.getOut().println("copy template to: " + mDestDir.getAbsolutePath());
        var templateName = mTask.getTemplateDirSnap().getName();
        var targetDir = new File(mDestDir, templateName);

        if (!mDryRun) {
            mInputOutput.getOut().println("copy zip contents to: " + targetDir.getAbsolutePath());
            cp(mTask.getTemplateDirSnap(), targetDir, false);
            cp(mLinuxTargetFile, targetDir, true);

            var preScriptFile = new File(targetDir, "exec_before");
            if (preScriptFile.isFile()) {
                mInputOutput.getOut().println("Run PRE SNAP execution script");
                executeScript(null, targetDir, preScriptFile);
            }

            var yaml = new File(targetDir, "snap/snapcraft.yaml");
            var lines = new ArrayList<String>();
            for (String line : FileUtils.readLines(yaml, "utf-8")) {
                lines.add(line.replaceAll("version: '.*'", String.format("version: '%s'", mVersion)));
            }

            FileUtils.writeLines(yaml, "utf-8", lines);

            var environment = new HashMap<String, String>();
            var command = new ArrayList<String>();
            command.add("snapcraft");
            for (var option : StringUtils.split(mOptions.get(OPT_SNAP_OPTIONS, ""))) {
                command.add(option);
            }

            execute(command, environment, targetDir);

            var postScriptFile = new File(targetDir, "exec_after");
            if (postScriptFile.isFile()) {
                mInputOutput.getOut().println("Run POST SNAP execution script");
                executeScript(null, targetDir, postScriptFile);
            }
        }
    }

    private void execute(Map<String, String> environment, File workingDirectory, String... commands) {
        execute(new ArrayList<>(Arrays.asList(commands)), environment, workingDirectory);
    }

    private void execute(ArrayList<String> command, Map<String, String> environment, File workingDirectory) {
        mInputOutput.getOut().println(getHeader() + String.join(" ", command));

        if (!mDryRun) {
            var processBuilder = new ProcessBuilder(command).inheritIO();
            processBuilder.redirectOutput(ProcessBuilder.Redirect.PIPE);
            if (environment != null) {
                processBuilder.environment().putAll(environment);
            }
            if (workingDirectory != null) {
                processBuilder.directory(workingDirectory);
            }
            try {
                mCurrentProcess = processBuilder.start();
//                new ProcessLogThread(mCurrentProcess.getInputStream(), 0, mLog).start();
//                new ProcessLogThread(mCurrentProcess.getErrorStream(), -1, mLog).start();
//                Thread.sleep(1000);
                mCurrentProcess.waitFor();
            } catch (IOException ex) {
//                mLog.timedErr(ex.getMessage());
            } catch (InterruptedException ex) {
                mCurrentProcess.destroy();
                mInterrupted = true;
            }
        }
    }

    private void executeScript(Map<String, String> environment, File workingDirectory, File script) {
        execute(environment, workingDirectory, script.getAbsolutePath());
    }

    private String getHeader() {
        return mDryRun ? "execute: (dry-run) " : "execute: ";
    }

    private boolean initTargetDirectory() throws IOException {
        boolean result = true;

        if (!mDestDir.exists()) {
            FileUtils.forceMkdir(mDestDir);
        } else {
            //TODO Implement confirmation dialog
            result = true;
//                result = MainPanel.getDialogListener().onDialogRequest("Clear existing directory?", String.format("Clear\n%s\nand continue?", mDestDir.getAbsolutePath()));
            if (result) {
                throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
//                    FileUtils.deleteQuietly(mDestDir);
//                    FileUtils.forceMkdir(mDestDir);
            }
        }

        return result;
    }

    private void jobEnded(OutputLineMode outputLineMode, String action) {
        mProgressHandle.finish();
        mMainFoldHandle.silentFinish();
        mStatusDisplayer.setStatusText(action);
        mOutputHelper.printSummary(outputLineMode, action, Dict.TASK.toString());
        ExecutorManager.getInstance().getExecutors().remove(mTask.getId());
    }

    private void removeBin(File file) throws IOException {
        mInputOutput.getOut().println("remove: " + file.getAbsolutePath());
        if (!mDryRun) {
            FileUtils.forceDelete(file);
        }
    }

    private void removeBin(File binDir, boolean keepWindows) throws IOException {
        if (mDryRun) {
            mInputOutput.getOut().println("remove non platform executable(s)");
        } else {
            for (var file : binDir.listFiles()) {
                boolean exe = FilenameUtils.getExtension(file.getName()).equalsIgnoreCase("exe");
                if ((keepWindows && !exe) || (!keepWindows && exe)) {
                    removeBin(file);
                }
            }
        }
    }

    private void unzip() throws IOException {
        mTempDir = Files.createTempDirectory("packager").toFile();
        mTempDir.deleteOnExit();
        mInputOutput.getOut().println("create temp dir: " + mTempDir.getAbsolutePath());
        mInputOutput.getOut().println("unzip: " + mTask.getSourceFile());
        if (!mDryRun) {
            //execute(null, "unzip", "-q", mTask.getSourceFile().getAbsolutePath(), "-d", mTempDir.getAbsolutePath());
            new ZipFile(mTask.getSourceFile()).extractAll(mTempDir.getAbsolutePath());
            mContentDir = mTempDir.list()[0];
        }
    }

}
