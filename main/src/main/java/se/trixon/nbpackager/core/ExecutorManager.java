/*
 * Copyright 2023 Patrik Karlström <patrik@trixon.se>.
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

import java.util.HashMap;
import java.util.ResourceBundle;
import org.openide.util.NbBundle;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;

/**
 *
 * @author Patrik Karlström <patrik@trixon.se>
 */
public class ExecutorManager {

    private final ResourceBundle mBundle = NbBundle.getBundle(ExecutorManager.class);
    private final HashMap<String, Executor> mExecutors = new HashMap<>();
    private InputOutput mInputOutput;
    private boolean mNoErrors = true;

    public static ExecutorManager getInstance() {
        return Holder.INSTANCE;
    }

    private ExecutorManager() {
    }

    public HashMap<String, Executor> getExecutors() {
        return mExecutors;
    }

    public void requestStart(Task task) {
        var executor = new Executor(task);
        mExecutors.put(task.getId(), executor);
        executor.run();

//        if (mInputOutput != null) {
//            try {
//                mInputOutput.getOut().reset();
//            } catch (IOException ex) {
//                Exceptions.printStackTrace(ex);
//            }
//        }
//
//        var filter = new FileNameExtensionFilter("Keyhole Markup Language (*.kml)", "kml");
//        SimpleDialog.clearFilters();
//        SimpleDialog.addFilter(filter);
//        SimpleDialog.setFilter(filter);
//        SimpleDialog.setParent(Almond.getFrame());
//        SimpleDialog.setTitle(String.format("%s %s", Dict.SAVE.toString(), task.getName()));
//
//        var destination = task.getDestinationFile();
//        if (destination == null) {
//            SimpleDialog.setPath(FileUtils.getUserDirectory());
//        } else {
//            SimpleDialog.setPath(destination.getParentFile());
//            SimpleDialog.setSelectedFile(destination);
//        }
//
//        if (SimpleDialog.saveFile()) {
//            var file = SimpleDialog.getPath();
//            task.setDestinationFile(file);
//
//            mNoErrors = task.isValid();
//            if (!mNoErrors) {
//                printErr(task, task.getValidationError());
//            }
//
//            if (!Files.isWritable(file.getParentFile().toPath())) {
//                printErr(task, mBundle.getString("insufficient_privileges").formatted(file.getAbsolutePath()));
//            }
//
//            if (!task.hasValidRelativeSourceDest()) {
//                printErr(task, mBundle.getString("invalid_relative_source_dest"));
//            }
//
//            if (mNoErrors) {
//                var executor = new Executor(task);
//                mExecutors.put(task.getId(), executor);
//                executor.run();
//            } else {
//                printErr(task, Dict.ABORTING.toString());
//            }
//        }
    }

    private void printErr(Task task, String s) {
        if (mInputOutput == null) {
            mInputOutput = IOProvider.getDefault().getIO(task.getName(), false);
            mInputOutput.select();
        }

        mInputOutput.getErr().println(s);

        mNoErrors = false;
    }

    private static class Holder {

        private static final ExecutorManager INSTANCE = new ExecutorManager();
    }

}
