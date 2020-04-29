/*
 * Copyright 2020 Patrik Karlström.
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
package se.trixon.nbpackager_core;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Locale;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.MessageDigestAlgorithms;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import se.trixon.almond.util.Log;
import se.trixon.almond.util.ProcessLogThread;

/**
 *
 * @author Patrik Karlström
 */
public class Operation {

    private Process mCurrentProcess;
    private final boolean mDryRun;
    private boolean mInterrupted;
    private final Log mLog;
    private final Profile mProfile;
    private File mTempDir;

    public Operation(Profile profile, Log log) {
        mProfile = profile;
        mLog = log;
        mDryRun = mProfile.isDryRun();
    }

    public void start() throws IOException {
        long startTime = System.currentTimeMillis();

        if (mProfile.getPreScript() != null) {
            mLog.out("Run PRE execution script");
            executeScript(mProfile.getPreScript());
        }

        if (!mInterrupted) {
            unzip();
        }

        if (!mInterrupted & mProfile.isTargetAny()) {
            createPackage("any");
        }

        if (!mInterrupted & mProfile.isTargetLinux()) {
            createPackage("linux");
        }

        if (!mInterrupted & mProfile.isTargetMac()) {
            createPackage("mac");
        }

        if (!mInterrupted & mProfile.isTargetWindows()) {
            createPackage("windows");
        }

        if (!mInterrupted && mProfile.getPostScript() != null) {
            mLog.out("Run POST execution script");
            executeScript(mProfile.getPostScript());
        }

        if (mInterrupted) {
            mLog.err("\nOperation interrupted");
        } else {
            mLog.out("\nOperation completed");
        }
    }

    private void createChecksum(File file, String algorithm) throws IOException {
        File digestFile = new File(file.getAbsolutePath() + String.format(".%s", StringUtils.remove(algorithm, "-").toLowerCase(Locale.getDefault())));
        mLog.out("create checksum: " + digestFile.getAbsolutePath());
        if (!mDryRun) {
            String digest = new DigestUtils(algorithm).digestAsHex(file);
            FileUtils.writeStringToFile(digestFile, String.format("%s  %s", digest, file.getName()), Charset.defaultCharset());
        }
    }

    private void createPackage(String target) throws IOException {
        mLog.out("create package: " + target);

        File targetDir = new File(mProfile.getDestDir(), target);

        mLog.out("copy zip contents to: " + targetDir.getAbsolutePath());
        if (!mDryRun) {
            targetDir.mkdirs();
            FileUtils.copyDirectory(mTempDir, targetDir, true);
        }

        if (mProfile.getResources() != null) {
            mLog.out("copy resources to: " + targetDir.getAbsolutePath());
            if (!mDryRun) {
                FileUtils.copyDirectory(mProfile.getResources(), targetDir, true);
            }
        }

        //TODO Add JRE
        File targetFile = new File(mProfile.getDestDir(), String.format("%s-%s.zip", mProfile.getBasename(), target));
        mLog.out("creating zip: " + targetFile.getAbsolutePath());
        if (!mDryRun) {
            ZipParameters zipParameters = new ZipParameters();
            zipParameters.setIncludeRootFolder(false);
            new ZipFile(targetFile.getAbsolutePath()).addFolder(targetDir, zipParameters);
        }

        if (mProfile.isChecksumSha256()) {
            createChecksum(targetFile, MessageDigestAlgorithms.SHA_256);
        }

        if (mProfile.isChecksumSha512()) {
            createChecksum(targetFile, MessageDigestAlgorithms.SHA_512);
        }
    }

    private void execute(ArrayList<String> command) {
        mLog.out(getHeader() + String.join(" ", command));

        if (!mDryRun) {
            ProcessBuilder processBuilder = new ProcessBuilder(command).inheritIO();
            processBuilder.redirectOutput(ProcessBuilder.Redirect.PIPE);
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

    private void executeScript(File script) {
        ArrayList<String> command = new ArrayList<>();
        command.add(script.getAbsolutePath());
        execute(command);
    }

    private String getHeader() {
        return mDryRun ? "execute: (dry-run) " : "execute: ";
    }

    private void unzip() throws IOException {
        mTempDir = Files.createTempDirectory("packager").toFile();
        mTempDir.deleteOnExit();
        mLog.out("create temp dir: " + mTempDir.getAbsolutePath());
        mLog.out("unzip: " + mProfile.getSourceFile());
        if (!mDryRun) {
            new ZipFile(mProfile.getSourceFile()).extractAll(mTempDir.getAbsolutePath());
        }
    }
}
