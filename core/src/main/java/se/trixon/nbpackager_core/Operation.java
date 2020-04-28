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

import se.trixon.almond.util.Log;

/**
 *
 * @author Patrik Karlström
 */
public class Operation {

    private boolean mInterrupted;
    private final Log mLog;
    private final Profile mProfile;

    public Operation(Profile profile, Log log) {
        mProfile = profile;
        mLog = log;
    }

    public void start() {
        long startTime = System.currentTimeMillis();
        mLog.timedOut("start & wait");
        try {
            Thread.sleep(10000);
        } catch (InterruptedException ex) {
            mInterrupted = true;
        }

        if (mInterrupted) {
            mLog.timedOut("Interrupted");
        } else {
            mLog.timedOut("done");
        }
    }

}
