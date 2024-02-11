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

import com.google.gson.annotations.SerializedName;
import java.util.UUID;
import se.trixon.almond.util.fx.control.editable_list.EditableListItem;

/**
 *
 * @author Patrik Karlström <patrik@trixon.se>
 */
public class Task implements EditableListItem {

    @SerializedName("description")
    private String mDescription;
    @SerializedName("name")
    private String mName;
    @SerializedName("uuid")
    private String mId = UUID.randomUUID().toString();
    @SerializedName("last_run")
    private long mLastRun;

    public Task() {
    }

    public String getDescription() {
        return mDescription;
    }

    public String getId() {
        return mId;
    }

    public long getLastRun() {
        return mLastRun;
    }

    @Override
    public String getName() {
        return mName;
    }

    public void setDescription(String description) {
        this.mDescription = description;
    }

    public void setId(String id) {
        this.mId = id;
    }

    public void setLastRun(long lastRun) {
        this.mLastRun = lastRun;
    }

    public void setName(String name) {
        mName = name;
    }

    public String toInfoString() {
        return "TODO toInfoString";
    }

}
