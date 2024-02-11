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

import java.util.function.Predicate;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import org.openide.DialogDescriptor;
import se.trixon.almond.util.Dict;
import se.trixon.almond.util.fx.FxHelper;
import se.trixon.almond.util.fx.control.FileChooserPane;
import se.trixon.nbpackager.core.StorageManager;
import se.trixon.nbpackager.core.Task;
import se.trixon.nbpackager.core.TaskManager;

/**
 *
 * @author Patrik Karlström <patrik@trixon.se>
 */
public class TaskEditor extends GridPane {

    private TextField mDescTextField;
    private FileChooserPane mDestChooserPane;
    private DialogDescriptor mDialogDescriptor;
    private FileChooserPane mJreLinuxChooserPane;
    private FileChooserPane mJreMacChooserPane;
    private FileChooserPane mJreWindowsChooserPane;
    private TextField mNameTextField;
    private FileChooserPane mResourceChooserPane;
    private FileChooserPane mScriptPostChooserPane;
    private FileChooserPane mScriptPreChooserPane;
    private CheckBox mSha256SumCheckBox;
    private CheckBox mSha512SumCheckBox;
    private FileChooserPane mSourceChooserPane;
    private CheckBox mTargetAnyCheckBox;
    private CheckBox mTargetLinuxAppImageCheckBox;
    private CheckBox mTargetLinuxCheckBox;
    private CheckBox mTargetLinuxSnapCheckBox;
    private CheckBox mTargetMacCheckBox;
    private CheckBox mTargetWindowsCheckBox;
    private Task mTask;
    private final TaskManager mTaskManager = TaskManager.getInstance();
    private FileChooserPane mTemplateDirAppImageChooserPane;
    private FileChooserPane mTemplateDirSnapChooserPane;
    private final ValidationSupport mValidationSupport = new ValidationSupport();

    public TaskEditor() {
        createUI();

        Platform.runLater(() -> {
            initValidation();
        });
    }

    public Task save() {
        mTaskManager.getIdToItem().put(mTask.getId(), mTask);

        mTask.setName(mNameTextField.getText().trim());
        mTask.setDescription(mDescTextField.getText());
        mTask.setSourceDir(mSourceChooserPane.getPath());
        mTask.setDestDir(mDestChooserPane.getPath());
        mTask.setScriptPre(mScriptPreChooserPane.getPath());
        mTask.setScriptPost(mScriptPostChooserPane.getPath());
        mTask.setTemplateDirAppImage(mTemplateDirAppImageChooserPane.getPath());
        mTask.setTemplateDirSnap(mTemplateDirSnapChooserPane.getPath());
        mTask.setResourceDir(mResourceChooserPane.getPath());

        mTask.setJreLinux(mJreLinuxChooserPane.getPath());
        mTask.setJreMac(mJreMacChooserPane.getPath());
        mTask.setJreWindows(mJreWindowsChooserPane.getPath());

        mTask.setTargetAny(mTargetAnyCheckBox.isSelected());
        mTask.setTargetLinux(mTargetLinuxCheckBox.isSelected());
        mTask.setTargetLinuxAppImage(mTargetLinuxAppImageCheckBox.isSelected());
        mTask.setTargetLinuxSnap(mTargetLinuxSnapCheckBox.isSelected());
        mTask.setTargetMac(mTargetMacCheckBox.isSelected());
        mTask.setTargetWindows(mTargetWindowsCheckBox.isSelected());

        mTask.setChecksumSha256(mSha256SumCheckBox.isSelected());
        mTask.setChecksumSha512(mSha512SumCheckBox.isSelected());

        StorageManager.save();

        return mTask;
    }

    void load(Task task, DialogDescriptor dialogDescriptor) {
        if (task == null) {
            task = new Task();
        }

        mDialogDescriptor = dialogDescriptor;
        mTask = task;

        mNameTextField.setText(task.getName());
        mDescTextField.setText(task.getDescription());

        mSourceChooserPane.setPath(task.getSourceDir());
        mDestChooserPane.setPath(task.getDestDir());

        mScriptPreChooserPane.setPath(task.getScriptPre());
        mScriptPostChooserPane.setPath(task.getScriptPost());

        mTemplateDirAppImageChooserPane.setPath(task.getTemplateDirAppImage());
        mTemplateDirSnapChooserPane.setPath(task.getTemplateDirSnap());
        mResourceChooserPane.setPath(task.getResourceDir());

        mJreLinuxChooserPane.setPath(task.getJreLinux());
        mJreMacChooserPane.setPath(task.getJreMac());
        mJreWindowsChooserPane.setPath(task.getJreWindows());

        mTargetLinuxCheckBox.setSelected(task.isTargetLinux());
        mTargetLinuxAppImageCheckBox.setSelected(task.isTargetLinuxAppImage());
        mTargetLinuxSnapCheckBox.setSelected(task.isTargetLinuxSnap());
        mTargetMacCheckBox.setSelected(task.isTargetMac());
        mTargetWindowsCheckBox.setSelected(task.isTargetWindows());
        mTargetAnyCheckBox.setSelected(task.isTargetAny());

        mSha256SumCheckBox.setSelected(task.isChecksumSha256());
        mSha512SumCheckBox.setSelected(task.isChecksumSha512());

        mNameTextField.requestFocus();
    }

    private void createUI() {
        setHgap(8);

        var nameLabel = new Label(Dict.NAME.toString());
        var descLabel = new Label(Dict.DESCRIPTION.toString());

        mNameTextField = new TextField();
        mDescTextField = new TextField();
        mSourceChooserPane = new FileChooserPane(Dict.SELECT.toString(), Dict.SOURCE.toString(), FileChooserPane.ObjectMode.DIRECTORY, SelectionMode.SINGLE);
        mDestChooserPane = new FileChooserPane(Dict.SELECT.toString(), Dict.DESTINATION.toString(), FileChooserPane.ObjectMode.DIRECTORY, SelectionMode.SINGLE);
        mScriptPreChooserPane = new FileChooserPane(Dict.SELECT.toString(), "PRE execution script", FileChooserPane.ObjectMode.FILE, SelectionMode.SINGLE);
        mScriptPostChooserPane = new FileChooserPane(Dict.SELECT.toString(), "POST execution script", FileChooserPane.ObjectMode.FILE, SelectionMode.SINGLE);
        mTemplateDirAppImageChooserPane = new FileChooserPane(Dict.SELECT.toString(), "AppImage template directory", FileChooserPane.ObjectMode.DIRECTORY, SelectionMode.SINGLE);
        mTemplateDirSnapChooserPane = new FileChooserPane(Dict.SELECT.toString(), "Snap template directory", FileChooserPane.ObjectMode.DIRECTORY, SelectionMode.SINGLE);
        mResourceChooserPane = new FileChooserPane(Dict.SELECT.toString(), "Resource base directory", FileChooserPane.ObjectMode.DIRECTORY, SelectionMode.SINGLE);

        mJreLinuxChooserPane = new FileChooserPane(Dict.SELECT.toString(), "Linux JRE", FileChooserPane.ObjectMode.DIRECTORY, SelectionMode.SINGLE);
        mJreMacChooserPane = new FileChooserPane(Dict.SELECT.toString(), "Mac JRE", FileChooserPane.ObjectMode.DIRECTORY, SelectionMode.SINGLE);
        mJreWindowsChooserPane = new FileChooserPane(Dict.SELECT.toString(), "Windows JRE", FileChooserPane.ObjectMode.DIRECTORY, SelectionMode.SINGLE);

        mTargetLinuxCheckBox = new CheckBox("Linux");
        mTargetLinuxAppImageCheckBox = new CheckBox("AppImage");
        mTargetLinuxSnapCheckBox = new CheckBox("Snap");
        mTargetMacCheckBox = new CheckBox("Mac");
        mTargetWindowsCheckBox = new CheckBox("Windows");
        mTargetAnyCheckBox = new CheckBox("Any (without JRE)");

        var spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        mSha256SumCheckBox = new CheckBox("sha256sum");
        mSha512SumCheckBox = new CheckBox("sha512sum");

        var checkBoxBox = new HBox(8,
                mTargetLinuxCheckBox,
                mTargetLinuxAppImageCheckBox,
                mTargetLinuxSnapCheckBox,
                mTargetMacCheckBox,
                mTargetWindowsCheckBox,
                mTargetAnyCheckBox,
                spacer,
                mSha256SumCheckBox,
                mSha512SumCheckBox
        );

        int row = 0;
        addRow(row++, nameLabel, descLabel);
        addRow(row++, mNameTextField, mDescTextField);
        add(mSourceChooserPane, 0, ++row, 1, 1);
        add(mDestChooserPane, 1, row, 1, 1);
        add(mScriptPreChooserPane, 0, ++row, 1, 1);
        add(mScriptPostChooserPane, 1, row, 1, 1);
        add(mTemplateDirAppImageChooserPane, 0, ++row, 1, 1);
        add(mTemplateDirSnapChooserPane, 1, row, 1, 1);
        add(mResourceChooserPane, 0, ++row, 1, 1);

        var jreGridPane = new GridPane();
        jreGridPane.setHgap(8);
        jreGridPane.addRow(0, mJreLinuxChooserPane, mJreMacChooserPane, mJreWindowsChooserPane);

        add(jreGridPane, 0, ++row, GridPane.REMAINING, 1);
        add(checkBoxBox, 0, ++row, GridPane.REMAINING, 1);

        GridPane.setHgrow(mNameTextField, Priority.ALWAYS);
        GridPane.setHgrow(mDescTextField, Priority.ALWAYS);
        GridPane.setHgrow(mJreLinuxChooserPane, Priority.ALWAYS);
        GridPane.setHgrow(mJreMacChooserPane, Priority.ALWAYS);
        GridPane.setHgrow(mJreWindowsChooserPane, Priority.ALWAYS);

        GridPane.setFillWidth(mNameTextField, true);
        GridPane.setFillWidth(mDescTextField, true);
        GridPane.setFillWidth(mJreLinuxChooserPane, true);
        GridPane.setFillWidth(mJreMacChooserPane, true);
        GridPane.setFillWidth(mJreWindowsChooserPane, true);

        mNameTextField.setPrefWidth(1000);
        mDescTextField.setPrefWidth(1000);

        FxHelper.setPadding(new Insets(8, 0, 0, 0),
                mSourceChooserPane,
                mDestChooserPane,
                mScriptPreChooserPane,
                mScriptPostChooserPane,
                mTemplateDirAppImageChooserPane,
                mTemplateDirSnapChooserPane,
                mResourceChooserPane,
                jreGridPane
        );

        FxHelper.setPadding(new Insets(18, 0, 0, 0),
                checkBoxBox
        );

        mTargetLinuxAppImageCheckBox.disableProperty().bind(mTargetLinuxCheckBox.selectedProperty().not());
        mTargetLinuxSnapCheckBox.disableProperty().bind(mTargetLinuxCheckBox.selectedProperty().not());
    }

    private void initValidation() {
        final String text_is_required = "Text is required";
        boolean indicateRequired = true;

        var namePredicate = (Predicate<String>) s -> {
            return mTask != null && mTaskManager.isValid(mTask.getName(), s);
        };

        mValidationSupport.registerValidator(mNameTextField, indicateRequired, Validator.createEmptyValidator(text_is_required));
        mValidationSupport.registerValidator(mNameTextField, indicateRequired, Validator.createPredicateValidator(namePredicate, text_is_required));

        mValidationSupport.validationResultProperty().addListener((p, o, n) -> {
            if (mDialogDescriptor != null) {
                mDialogDescriptor.setValid(!mValidationSupport.isInvalid());

            }
        });

        mValidationSupport.initInitialDecoration();
    }

}
