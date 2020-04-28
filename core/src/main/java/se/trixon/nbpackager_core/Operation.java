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
import java.nio.file.Files;
import java.util.ArrayList;
import net.lingala.zip4j.ZipFile;
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
