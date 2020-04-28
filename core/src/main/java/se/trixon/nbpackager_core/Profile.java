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
import java.io.FilenameFilter;
import java.util.LinkedHashMap;
import org.apache.commons.lang3.StringUtils;
import se.trixon.almond.util.BooleanHelper;

/**
 *
 * @author Patrik Karlström
 */
public class Profile {

    private File mAppImageTemplate;
    private boolean mChecksumSha256;
    private boolean mChecksumSha512;
    private File mDestDir;
    private boolean mDryRun;
    private File mJreLinux;
    private File mJreMac;
    private File mJreWindows;
    private File mPostScript;
    private File mPreScript;
    private File mResources;
    private File mSourceDir;
    private File mSourceFile;
    private boolean mTargetAny;
    private boolean mTargetAppImage;
    private boolean mTargetLinux;
    private boolean mTargetMac;
    private boolean mTargetWindows;
    private StringBuilder mValidationErrorBuilder;

    public Profile() {
    }

    public File getAppImageTemplate() {
        return mAppImageTemplate;
    }

    public File getDestDir() {
        return mDestDir;
    }

    public File getJreLinux() {
        return mJreLinux;
    }

    public File getJreMac() {
        return mJreMac;
    }

    public File getJreWindows() {
        return mJreWindows;
    }

    public File getPostScript() {
        return mPostScript;
    }

    public File getPreScript() {
        return mPreScript;
    }

    public File getResources() {
        return mResources;
    }

    public File getSourceDir() {
        return mSourceDir;
    }

    public File getSourceFile() {
        return mSourceFile;
    }

    public String getValidationError() {
        return mValidationErrorBuilder.toString();
    }

    public boolean isChecksumSha256() {
        return mChecksumSha256;
    }

    public boolean isChecksumSha512() {
        return mChecksumSha512;
    }

    public boolean isDryRun() {
        return mDryRun;
    }

    public boolean isTargetAny() {
        return mTargetAny;
    }

    public boolean isTargetAppImage() {
        return mTargetAppImage;
    }

    public boolean isTargetLinux() {
        return mTargetLinux;
    }

    public boolean isTargetMac() {
        return mTargetMac;
    }

    public boolean isTargetWindows() {
        return mTargetWindows;
    }

    public boolean isValid() {
        mValidationErrorBuilder = new StringBuilder();

        if (mSourceDir == null || !mSourceDir.isDirectory()) {
            addValidationError("invalid source directory: " + mSourceDir);
        }

        if (!validSourceFile()) {
            addValidationError("no zip found in " + mSourceDir);
        }

        if (mDestDir == null || mDestDir.exists()) {
            addValidationError("invalid destination directory: " + mDestDir);
        }

        if (mPreScript != null && !mPreScript.isFile()) {
            addValidationError("invalid pre script: " + mPreScript);
        }

        if (mPostScript != null && !mPostScript.isFile()) {
            addValidationError("invalid post script: " + mPostScript);
        }

        if (mResources != null && !mResources.isDirectory()) {
            addValidationError("invalid resource directory: " + mResources);
        }

        if (mAppImageTemplate != null && !mAppImageTemplate.isDirectory()) {
            addValidationError("invalid AppImage template directory: " + mAppImageTemplate);
        }

        if (mJreLinux != null && !mJreLinux.isDirectory()) {
            addValidationError("invalid Linux JRE: " + mJreLinux);
        }

        if (mJreMac != null && !mJreMac.isDirectory()) {
            addValidationError("invalid Mac JRE: " + mJreMac);
        }

        if (mJreWindows != null && !mJreWindows.isDirectory()) {
            addValidationError("invalid Windows JRE: " + mJreWindows);
        }

        if (mTargetAppImage
                && (mAppImageTemplate == null || !mAppImageTemplate.isDirectory())
                && (mJreLinux == null || !mJreLinux.isDirectory())) {
            addValidationError("invalid target: AppImage");
        }

        if (mTargetLinux
                && (mJreLinux == null || !mJreLinux.isDirectory())) {
            addValidationError("invalid target: Linux");
        }

        if (mTargetMac
                && (mJreMac == null || !mJreMac.isDirectory())) {
            addValidationError("invalid target: Mac");
        }

        if (mTargetWindows
                && (mJreWindows == null || !mJreWindows.isDirectory())) {
            addValidationError("invalid target: Windows");
        }

        if ((mTargetAppImage || mTargetLinux || mTargetMac || mTargetWindows || mTargetAny) == false) {
            addValidationError("invalid target: NO TARGET SELECTED");
        }

        return mValidationErrorBuilder.length() == 0;
    }

    public void setAppImageTemplate(File appImageTemplate) {
        mAppImageTemplate = appImageTemplate;
    }

    public void setChecksumSha256(boolean checksumSha256) {
        mChecksumSha256 = checksumSha256;
    }

    public void setChecksumSha512(boolean checksumSha512) {
        mChecksumSha512 = checksumSha512;
    }

    public void setDestDir(File destDir) {
        mDestDir = destDir;
    }

    public void setDryRun(boolean dryRun) {
        mDryRun = dryRun;
    }

    public void setJreLinux(File jreLinux) {
        mJreLinux = jreLinux;
    }

    public void setJreMac(File jreMac) {
        mJreMac = jreMac;
    }

    public void setJreWindows(File jreWindows) {
        mJreWindows = jreWindows;
    }

    public void setPostScript(File postScript) {
        mPostScript = postScript;
    }

    public void setPreScript(File preScript) {
        mPreScript = preScript;
    }

    public void setResources(File resources) {
        mResources = resources;
    }

    public void setSourceDir(File sourceDir) {
        mSourceDir = sourceDir;
    }

    public void setTargetAny(boolean targetAny) {
        mTargetAny = targetAny;
    }

    public void setTargetAppImage(boolean targetAppImage) {
        mTargetAppImage = targetAppImage;
    }

    public void setTargetLinux(boolean targetLinux) {
        mTargetLinux = targetLinux;
    }

    public void setTargetMac(boolean targetMac) {
        mTargetMac = targetMac;
    }

    public void setTargetWindows(boolean targetWindows) {
        mTargetWindows = targetWindows;
    }

    public String toDebugString() {
        LinkedHashMap<String, String> values = new LinkedHashMap<>();
        values.put("Source", fileToString(mSourceDir));
        values.put("Destination", fileToString(mDestDir));
        values.put("PRE execution", fileToString(mPreScript));
        values.put("POST execution", fileToString(mPostScript));
        values.put("Resources", fileToString(mResources));
        values.put("AppImage template", fileToString(mAppImageTemplate));
        values.put(" ", "");
        values.put("JRE", "");
        values.put(" Linux", fileToString(mJreLinux));
        values.put(" Mac", fileToString(mJreMac));
        values.put(" Windows", fileToString(mJreWindows));
        values.put("  ", "");
        values.put("Target  ", "");
        values.put(" AppImage", BooleanHelper.asYesNo(mTargetAppImage));
        values.put(" Linux ", BooleanHelper.asYesNo(mTargetLinux));
        values.put(" Mac ", BooleanHelper.asYesNo(mTargetMac));
        values.put(" Windows ", BooleanHelper.asYesNo(mTargetWindows));
        values.put(" Any", BooleanHelper.asYesNo(mTargetAny));
        values.put("   ", "");
        values.put("Checksum", "");
        values.put(" sha256", BooleanHelper.asYesNo(mChecksumSha256));
        values.put(" sha512", BooleanHelper.asYesNo(mChecksumSha512));

        int maxLength = Integer.MIN_VALUE;
        for (var key : values.keySet()) {
            maxLength = Math.max(maxLength, key.length());
        }
        maxLength += 1;

        String separator = " : ";
        StringBuilder builder = new StringBuilder();
        for (var entry : values.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            builder.append(StringUtils.rightPad(key, maxLength)).append(separator).append(value).append("\n");
        }

        return builder.toString();
    }

    private void addValidationError(String string) {
        mValidationErrorBuilder.append(string).append("\n");
    }

    private String fileToString(File file) {
        if (file == null) {
            return "";
        } else {
            return file.getAbsolutePath();
        }
    }

    private boolean validSourceFile() {
        FilenameFilter filter = (dir, name) -> name.endsWith(".zip");

        try {
            mSourceFile = new File(mSourceDir, mSourceDir.list(filter)[0]);
        } catch (Exception e) {
            mSourceFile = null;
        }

        return mSourceFile != null && mSourceFile.isFile();
    }
}
