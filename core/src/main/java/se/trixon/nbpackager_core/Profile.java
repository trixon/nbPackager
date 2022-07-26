/*
 * Copyright 2021 Patrik Karlström.
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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import java.io.File;
import java.io.FilenameFilter;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import se.trixon.almond.util.BooleanHelper;
import static se.trixon.nbpackager_core.Options.DEFAULT_APP_IMAGE_TOOL;
import static se.trixon.nbpackager_core.Options.OPT_APP_IMAGE_TOOL;

/**
 *
 * @author Patrik Karlström
 */
public class Profile implements Comparable<Profile>, Cloneable {

    private static final Gson GSON = new GsonBuilder()
            .setVersion(1.0)
            .serializeNulls()
            .setPrettyPrinting()
            .create();

    private transient String mBasename;
    @SerializedName("checksum256")
    private boolean mChecksumSha256;
    @SerializedName("checksum512")
    private boolean mChecksumSha512;
    @SerializedName("description")
    private String mDescription;
    @SerializedName("destDir")
    private File mDestDir;
    private transient boolean mDryRun;
    @SerializedName("jreLinux")
    private File mJreLinux;
    @SerializedName("jreMac")
    private File mJreMac;
    @SerializedName("jreWindows")
    private File mJreWindows;
    @SerializedName("last_run")
    private long mLastRun;
    @SerializedName("name")
    private String mName;
    private final transient Options mOptions = Options.getInstance();
    @SerializedName("resourceDir")
    private File mResourceDir;
    @SerializedName("scriptPost")
    private File mScriptPost;
    @SerializedName("scriptPre")
    private File mScriptPre;
    @SerializedName("sourceDir")
    private File mSourceDir;
    private transient File mSourceFile;
    @SerializedName("targetAny")
    private boolean mTargetAny;
    @SerializedName("targetLinux")
    private boolean mTargetLinux;
    @SerializedName("targetLinuxAppImage")
    private boolean mTargetLinuxAppImage;
    @SerializedName("targetLinuxSnap")
    private boolean mTargetLinuxSnap;
    @SerializedName("targetMac")
    private boolean mTargetMac;
    @SerializedName("targetWindows")
    private boolean mTargetWindows;
    @SerializedName("templateDirAppImage")
    private File mTemplateDirAppImage;
    @SerializedName("templateDirSnap")
    private File mTemplateDirSnap;
    private transient StringBuilder mValidationErrorBuilder;

    public Profile() {
    }

    @Override
    public Profile clone() {
        try {
            super.clone();
            String json = GSON.toJson(this);
            return GSON.fromJson(json, Profile.class);
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(Profile.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    @Override
    public int compareTo(Profile o) {
        return mName.compareTo(o.getName());
    }

    public String getBasename() {
        return mBasename;
    }

    public String getDescription() {
        return mDescription;
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

    public long getLastRun() {
        return mLastRun;
    }

    public String getName() {
        return mName;
    }

    public File getResourceDir() {
        return mResourceDir;
    }

    public File getScriptPost() {
        return mScriptPost;
    }

    public File getScriptPre() {
        return mScriptPre;
    }

    public File getSourceDir() {
        return mSourceDir;
    }

    public File getSourceFile() {
        return mSourceFile;
    }

    public File getTemplateDirAppImage() {
        return mTemplateDirAppImage;
    }

    public File getTemplateDirSnap() {
        return mTemplateDirSnap;
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

    public boolean isTargetLinux() {
        return mTargetLinux;
    }

    public boolean isTargetLinuxAppImage() {
        return mTargetLinuxAppImage;
    }

    public boolean isTargetLinuxSnap() {
        return mTargetLinuxSnap;
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

        if (mDestDir == null || !mDestDir.isDirectory()) {
            addValidationError("invalid destination directory: " + mDestDir);
        }

        if (mScriptPre != null && !mScriptPre.isFile()) {
            addValidationError("invalid pre script: " + mScriptPre);
        }

        if (mScriptPost != null && !mScriptPost.isFile()) {
            addValidationError("invalid post script: " + mScriptPost);
        }

        if (mResourceDir != null && !mResourceDir.isDirectory()) {
            addValidationError("invalid resource directory: " + mResourceDir);
        }

        if (mTemplateDirAppImage != null && !mTemplateDirAppImage.isDirectory()) {
            addValidationError("invalid AppImage template directory: " + mTemplateDirAppImage);
        }

        if (mTemplateDirSnap != null && !mTemplateDirSnap.isDirectory()) {
            addValidationError("invalid Snap template directory: " + mTemplateDirSnap);
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

        if (mTargetLinuxAppImage
                && (mTemplateDirAppImage == null || !mTemplateDirAppImage.isDirectory())
                && (mJreLinux == null || !mJreLinux.isDirectory())) {
            addValidationError("invalid target: AppImage");
        }

        if (mTargetLinuxSnap
                && (mTemplateDirSnap == null || !mTemplateDirSnap.isDirectory())
                && (mJreLinux == null || !mJreLinux.isDirectory())) {
            addValidationError("invalid target: Snap");
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

        if ((mTargetLinuxAppImage || mTargetLinuxSnap || mTargetLinux || mTargetMac || mTargetWindows || mTargetAny) == false) {
            addValidationError("invalid target: NO TARGET SELECTED");
        }

        if (mTargetLinuxAppImage) {
            File appImageTool = new File(mOptions.get(OPT_APP_IMAGE_TOOL, DEFAULT_APP_IMAGE_TOOL));
            if (!appImageTool.isFile()) {
                addValidationError(String.format("invalid appimagetool: Check your settings (%s)", appImageTool.getAbsolutePath()));
            }
        }

        return mValidationErrorBuilder.length() == 0;
    }

    public void setChecksumSha256(boolean checksumSha256) {
        mChecksumSha256 = checksumSha256;
    }

    public void setChecksumSha512(boolean checksumSha512) {
        mChecksumSha512 = checksumSha512;
    }

    public void setDescription(String description) {
        mDescription = description;
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

    public void setLastRun(long lastRun) {
        mLastRun = lastRun;
    }

    public void setName(String name) {
        mName = name;
    }

    public void setResourceDir(File resourceDir) {
        mResourceDir = resourceDir;
    }

    public void setScriptPost(File scriptPost) {
        mScriptPost = scriptPost;
    }

    public void setScriptPre(File scriptPre) {
        mScriptPre = scriptPre;
    }

    public void setSourceDir(File sourceDir) {
        mSourceDir = sourceDir;
    }

    public void setTargetAny(boolean targetAny) {
        mTargetAny = targetAny;
    }

    public void setTargetLinux(boolean targetLinux) {
        mTargetLinux = targetLinux;
    }

    public void setTargetLinuxAppImage(boolean targetLinuxAppImage) {
        mTargetLinuxAppImage = targetLinuxAppImage;
    }

    public void setTargetLinuxSnap(boolean targetLinuxSnap) {
        mTargetLinuxSnap = targetLinuxSnap;
    }

    public void setTargetMac(boolean targetMac) {
        mTargetMac = targetMac;
    }

    public void setTargetWindows(boolean targetWindows) {
        mTargetWindows = targetWindows;
    }

    public void setTemplateDirAppImage(File templateDirAppImage) {
        mTemplateDirAppImage = templateDirAppImage;
    }

    public void setTemplateDirSnap(File templateDirSnap) {
        mTemplateDirSnap = templateDirSnap;
    }

    public String toDebugString() {
        LinkedHashMap<String, String> values = new LinkedHashMap<>();
        values.put("Source", fileToString(mSourceDir));
        values.put("Destination", fileToString(mDestDir));
        values.put("PRE execution", fileToString(mScriptPre));
        values.put("POST execution", fileToString(mScriptPost));
        values.put("Resources", fileToString(mResourceDir));
        values.put("AppImage template", fileToString(mTemplateDirAppImage));
        values.put("Snap template", fileToString(mTemplateDirSnap));
        values.put(" ", "");
        values.put("JRE", "");
        values.put(" Linux", fileToString(mJreLinux));
        values.put(" Mac", fileToString(mJreMac));
        values.put(" Windows", fileToString(mJreWindows));
        values.put("  ", "");
        values.put("Target  ", "");
        values.put(" AppImage", BooleanHelper.asYesNo(mTargetLinuxAppImage));
        values.put(" Snap", BooleanHelper.asYesNo(mTargetLinuxSnap));
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
            String firstFilename = mSourceDir.list(filter)[0];
            mBasename = FilenameUtils.getBaseName(firstFilename);
            mSourceFile = new File(mSourceDir, firstFilename);
        } catch (Exception e) {
            mSourceFile = null;
        }

        return mSourceFile != null && mSourceFile.isFile();
    }
}
