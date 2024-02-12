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
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.lingala.zip4j.ZipFile;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.MessageDigestAlgorithms;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import se.trixon.almond.util.Log;
import se.trixon.almond.util.ProcessLogThread;
import se.trixon.nbpackager.Options;
import static se.trixon.nbpackager.Options.*;

/**
 *
 * @author Patrik Karlström <patrik@trixon.se>
 */
public class Executor implements Runnable {

    private final Options mOptions = Options.getInstance();

    public Executor(Task task) {
    }

    @Override
    public void run() {
        System.out.println("RUN");
    }

    class Operation {

        private String mContentDir = "NOT_AVAILABLE_IN_DRY_RUN";
        private Process mCurrentProcess;
        private File mDestDir;
        private final boolean mDryRun;
        private boolean mInterrupted;
        private File mLinuxTargetFile;
        private final Log mLog;
        private final Task mTask;
        private File mTempDir;
        private final String mVersion;

        public Operation(Task profile, Log log) {
            mTask = profile;
            mLog = log;
            mDryRun = mTask.isDryRun();

            mVersion = StringUtils.substringAfter(mTask.getBasename(), "-");
        }

        public void start() throws IOException {
            mDestDir = new File(mTask.getDestDir(), FilenameUtils.getBaseName(mTask.getSourceFile().getName()));

            if (!mDryRun) {
                if (!initTargetDirectory()) {
                    mLog.err("\nOperation cancelled");
                    return;
                }
            }

            if (mTask.getScriptPre() != null) {
                mLog.out("Run PRE execution script");
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
                mLog.out("Run POST execution script");
                executeScript(null, null, mTask.getScriptPost());
            }

            FileUtils.deleteDirectory(mTempDir);

            if (mInterrupted) {
                mLog.err("\nOperation interrupted");
            } else {
                mLog.out("\nOperation completed" + (mDryRun ? " (dry-run)" : ""));
            }
        }

        private void copyJre(File jreDir, File targetDir) throws IOException {
            if (jreDir == null) {
                mLog.out("No jre specified.");
                return;
            }
            String etc = String.format("etc/%s.conf", mContentDir);
            String jreName = jreDir.getName();
            File destDir = new File(targetDir, jreName);
            File etcFile = new File(targetDir, etc);
            mLog.out("set jdkhome in " + etcFile.getAbsolutePath());
            mLog.out("copy jre to: " + destDir.getAbsolutePath());
            if (!mDryRun) {
                String etcContent = FileUtils.readFileToString(etcFile, "utf-8");
                String key = StringUtils.contains(etcContent, "netbeans_jdkhome") ? "netbeans_jdkhome" : "jdkhome";
                FileUtils.write(etcFile, String.format("\n\n# Added by Packager\n%s=\"%s\"\n", key, jreName), "utf-8", true);
                cp(jreDir, destDir, false);
            }
        }

        private void cp(File source, File dest, boolean contentOnly) {
            String sourcePath = source.getAbsolutePath();
            if (contentOnly && source.isDirectory()) {
                sourcePath += "/.";
            }
            execute(null, null, "cp", "-ra", sourcePath, dest.getAbsolutePath());
            //FileUtils.copyDirectory(source, dest, true);
        }

        private void createChecksum(File file, String algorithm) throws IOException {
            File digestFile = new File(file.getAbsolutePath() + String.format(".%s", StringUtils.remove(algorithm, "-").toLowerCase(Locale.getDefault())));
            mLog.out("create checksum: " + digestFile.getAbsolutePath());
            if (!mDryRun) {
                String digest = new DigestUtils(algorithm).digestAsHex(file);
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
            mLog.out("\ncreate package: " + target);

            File targetDir = new File(mDestDir, target);

            mLog.out("copy zip contents to: " + targetDir.getAbsolutePath());
            if (!mDryRun) {
                targetDir.mkdirs();
                cp(mTempDir, targetDir, true);
            }

            File baseDir = mTask.getResourceDir();
            targetDir = new File(targetDir, mContentDir);
            if (baseDir != null) {
                mLog.out("copy resources to: " + targetDir.getAbsolutePath());
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

            mLog.out("creating zip: " + targetFile.getAbsolutePath());
            execute(null, targetDir.getParentFile(), "zip", "-qr", targetFile.getAbsolutePath(), contentDir);

            createChecksums(targetFile);
        }

        private void createPackageAppImage() throws IOException {
            mLog.out("\ncreate package: AppImage");
            mLog.out("copy template to: " + mDestDir.getAbsolutePath());
            String templateName = mTask.getTemplateDirAppImage().getName();
            templateName = StringUtils.replace(templateName, "__", String.format("-%s-", mVersion));
            var targetDir = new File(mDestDir, templateName);
            var targetFile = new File(mDestDir, StringUtils.replace(templateName, "AppDir", "AppImage"));

            if (!mDryRun) {
                cp(mTask.getTemplateDirAppImage(), targetDir, false);
            }

            var usrDir = new File(targetDir, "usr");
            mLog.out("copy zip contents to: " + usrDir.getAbsolutePath());
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
            mLog.out("\ncreate package: Snap");
            mLog.out("copy template to: " + mDestDir.getAbsolutePath());
            String templateName = mTask.getTemplateDirSnap().getName();
            File targetDir = new File(mDestDir, templateName);

            if (!mDryRun) {
                mLog.out("copy zip contents to: " + targetDir.getAbsolutePath());
                cp(mTask.getTemplateDirSnap(), targetDir, false);
                cp(mLinuxTargetFile, targetDir, true);

                var preScriptFile = new File(targetDir, "exec_before");
                if (preScriptFile.isFile()) {
                    mLog.out("Run PRE SNAP execution script");
                    executeScript(null, targetDir, preScriptFile);
                }

                File yaml = new File(targetDir, "snap/snapcraft.yaml");
                List<String> lines = new ArrayList<>();
                for (String line : FileUtils.readLines(yaml, "utf-8")) {
                    lines.add(line.replaceAll("version: '.*'", String.format("version: '%s'", mVersion)));
                }

                FileUtils.writeLines(yaml, "utf-8", lines);

                HashMap<String, String> environment = new HashMap<>();
                ArrayList<String> command = new ArrayList<>();
                command.add("snapcraft");
                for (String option : StringUtils.split(mOptions.get(OPT_SNAP_OPTIONS, ""))) {
                    command.add(option);
                }

                execute(command, environment, targetDir);

                var postScriptFile = new File(targetDir, "exec_after");
                if (postScriptFile.isFile()) {
                    mLog.out("Run POST SNAP execution script");
                    executeScript(null, targetDir, postScriptFile);
                }
            }
        }

        private void execute(Map<String, String> environment, File workingDirectory, String... commands) {
            execute(new ArrayList<>(Arrays.asList(commands)), environment, workingDirectory);
        }

        private void execute(ArrayList<String> command, Map<String, String> environment, File workingDirectory) {
            mLog.out(getHeader() + String.join(" ", command));

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
                    new ProcessLogThread(mCurrentProcess.getInputStream(), 0, mLog).start();
                    new ProcessLogThread(mCurrentProcess.getErrorStream(), -1, mLog).start();
                    Thread.sleep(1000);
                    mCurrentProcess.waitFor();
                } catch (IOException ex) {
                    mLog.timedErr(ex.getMessage());
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

        private void removeBin(File file) throws IOException {
            mLog.out("remove: " + file.getAbsolutePath());
            if (!mDryRun) {
                FileUtils.forceDelete(file);
            }
        }

        private void removeBin(File binDir, boolean keepWindows) throws IOException {
            if (mDryRun) {
                mLog.out("remove non platform executable(s)");
            } else {
                for (File file : binDir.listFiles()) {
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
            mLog.out("create temp dir: " + mTempDir.getAbsolutePath());
            mLog.out("unzip: " + mTask.getSourceFile());
            if (!mDryRun) {
                //execute(null, "unzip", "-q", mTask.getSourceFile().getAbsolutePath(), "-d", mTempDir.getAbsolutePath());
                new ZipFile(mTask.getSourceFile()).extractAll(mTempDir.getAbsolutePath());
                mContentDir = mTempDir.list()[0];
            }
        }
    }

}
