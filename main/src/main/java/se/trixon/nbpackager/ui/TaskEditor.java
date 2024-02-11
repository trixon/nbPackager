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
package se.trixon.nbpackager.ui;

import javafx.scene.layout.BorderPane;
import org.openide.DialogDescriptor;
import se.trixon.nbpackager.core.StorageManager;
import se.trixon.nbpackager.core.Task;
import se.trixon.nbpackager.core.TaskManager;

/**
 *
 * @author Patrik Karlström <patrik@trixon.se>
 */
public class TaskEditor extends BorderPane {

    private DialogDescriptor mDialogDescriptor;
    private Task mTask;
    private final TaskManager mTaskManager = TaskManager.getInstance();

    public TaskEditor() {
    }

    void load(Task task, DialogDescriptor dialogDescriptor) {
        if (task == null) {
            task = new Task();
        }

        mDialogDescriptor = dialogDescriptor;
        mTask = task;
    }

    public Task save() {
        mTaskManager.getIdToItem().put(mTask.getId(), mTask);

        StorageManager.save();

        return mTask;
    }

}
