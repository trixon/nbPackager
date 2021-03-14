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

import se.trixon.almond.util.OptionsBase;

/**
 *
 * @author Patrik Karlström
 */
public class Options extends OptionsBase {

    public static final String KEY_PROFILES = "profiles";
    public static final String KEY_PROFILE_APP_IMAGE_TEMPLATE = "app_image_template";
    public static final String KEY_PROFILE_CHECKSUM_SHA256 = "checksum.sha256";
    public static final String KEY_PROFILE_CHECKSUM_SHA512 = "checksum.sha512";
    public static final String KEY_PROFILE_DEST_DIR = "dest";
    public static final String KEY_PROFILE_JRE_LINUX = "jre.linux";
    public static final String KEY_PROFILE_JRE_MAC = "jre.mac";
    public static final String KEY_PROFILE_JRE_WINDOWS = "jre.windows";
    public static final String KEY_PROFILE_RESOURCES = "resources";
    public static final String KEY_PROFILE_SCRIPT_POST = "script.post";
    public static final String KEY_PROFILE_SCRIPT_PRE = "script.pre";
    public static final String KEY_PROFILE_SOURCE_DIR = "source";
    public static final String KEY_PROFILE_TARGET_ANY = "target.any";
    public static final String KEY_PROFILE_TARGET_APP_IMAGE = "target.appimae";
    public static final String KEY_PROFILE_TARGET_LINUX = "target.linux";
    public static final String KEY_PROFILE_TARGET_MAC = "target.mac";
    public static final String KEY_PROFILE_TARGET_WINDOWS = "target.windows";
    public static final String OPT_APP_IMAGE_OPTIONS = "appImage.options";
    public static final String OPT_APP_IMAGE_TOOL = "appImage.tool";
    public static final String DEFAULT_APP_IMAGE_TOOL = "/path/to/appimagetool-x86_64.AppImage";
    public static final String DEFAULT_APP_IMAGE_OPTIONS = "-nv";

    public static Options getInstance() {
        return OptionsHolder.INSTANCE;
    }

    private Options() {
    }

    private static class OptionsHolder {

        private static final Options INSTANCE = new Options();
    }
}
