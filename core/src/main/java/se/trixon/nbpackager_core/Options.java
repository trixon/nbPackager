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

import se.trixon.almond.util.OptionsBase;

/**
 *
 * @author Patrik Karlström
 */
public class Options extends OptionsBase {

    public static final String KEY_PROFILES = "profiles";
    public static final String KEY_PROFILE_SOURCE = "source";
    public static final String OPT_APP_IMAGE_OPTIONS = "appImage.options";
    public static final String OPT_APP_IMAGE_TOOL = "appImage.tool";

    public static Options getInstance() {
        return OptionsHolder.INSTANCE;
    }

    private Options() {
    }

    private static class OptionsHolder {

        private static final Options INSTANCE = new Options();
    }
}
