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
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javax.swing.JFileChooser;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import org.openide.DialogDescriptor;
import se.trixon.almond.nbp.Almond;
import se.trixon.almond.util.Dict;
import se.trixon.almond.util.fx.FxHelper;
import se.trixon.almond.util.fx.control.CheckedTab;
import se.trixon.almond.util.fx.control.FileChooserPaneSwingFx;
import se.trixon.nbpackager.core.StorageManager;
import se.trixon.nbpackager.core.Task;
import se.trixon.nbpackager.core.TaskManager;

/**
 *
 * @author Patrik Karlström <patrik@trixon.se>
 */
public class TaskEditor extends GridPane {

    private CheckedTab mAnyCheckedTab;
    private TextField mDescTextField;
    private final FileChooserPaneSwingFx mDestChooserPane = new FileChooserPaneSwingFx(Dict.SELECT.toString(), Dict.DESTINATION.toString(), Almond.getFrame(), JFileChooser.DIRECTORIES_ONLY);
    private DialogDescriptor mDialogDescriptor;
    private final FileChooserPaneSwingFx mJreLinuxChooserPane = new FileChooserPaneSwingFx(Dict.SELECT.toString(), "JRE", Almond.getFrame(), JFileChooser.DIRECTORIES_ONLY);
    private final FileChooserPaneSwingFx mJreMacChooserPane = new FileChooserPaneSwingFx(Dict.SELECT.toString(), "JRE", Almond.getFrame(), JFileChooser.DIRECTORIES_ONLY);
    private final FileChooserPaneSwingFx mJreWindowsChooserPane = new FileChooserPaneSwingFx(Dict.SELECT.toString(), "JRE", Almond.getFrame(), JFileChooser.DIRECTORIES_ONLY);
    private CheckedTab mLinuxCheckedTab;
    private CheckedTab mMacCheckedTab;
    private TextField mNameTextField;
    private final FileChooserPaneSwingFx mResourceChooserPane = new FileChooserPaneSwingFx(Dict.SELECT.toString(), "Resource base directory", Almond.getFrame(), JFileChooser.DIRECTORIES_ONLY);
    private final FileChooserPaneSwingFx mScriptPostChooserPane = new FileChooserPaneSwingFx(Dict.SELECT.toString(), Almond.getFrame(), JFileChooser.FILES_ONLY, "POST execution script");
    private final FileChooserPaneSwingFx mScriptPreChooserPane = new FileChooserPaneSwingFx(Dict.SELECT.toString(), Almond.getFrame(), JFileChooser.FILES_ONLY, "PRE execution script");
    private CheckBox mSha256SumCheckBox;
    private CheckBox mSha512SumCheckBox;
    private final FileChooserPaneSwingFx mSourceChooserPane = new FileChooserPaneSwingFx(Dict.SELECT.toString(), Dict.SOURCE.toString(), Almond.getFrame(), JFileChooser.DIRECTORIES_ONLY);
    private final TabPane mTabPane = new TabPane();
    private Task mTask;
    private final TaskManager mTaskManager = TaskManager.getInstance();
    private final FileChooserPaneSwingFx mTemplateDirAppImageChooserPane = new FileChooserPaneSwingFx(Dict.SELECT.toString(), Almond.getFrame(), JFileChooser.DIRECTORIES_ONLY, "AppImage template directory");
    private final FileChooserPaneSwingFx mTemplateDirSnapChooserPane = new FileChooserPaneSwingFx(Dict.SELECT.toString(), Almond.getFrame(), JFileChooser.DIRECTORIES_ONLY, "Snap template directory");
    private final ValidationSupport mValidationSupport = new ValidationSupport();
    private CheckedTab mWindowsCheckedTab;

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
        mTask.setExecuteScriptPre(mScriptPreChooserPane.getCheckBox().isSelected());
        mTask.setScriptPre(mScriptPreChooserPane.getPath());
        mTask.setExecuteScriptPost(mScriptPostChooserPane.getCheckBox().isSelected());
        mTask.setScriptPost(mScriptPostChooserPane.getPath());
        mTask.setTemplateDirAppImage(mTemplateDirAppImageChooserPane.getPath());
        mTask.setTemplateDirSnap(mTemplateDirSnapChooserPane.getPath());
        mTask.setResourceDir(mResourceChooserPane.getPath());

        mTask.setJreLinux(mJreLinuxChooserPane.getPath());
        mTask.setJreMac(mJreMacChooserPane.getPath());
        mTask.setJreWindows(mJreWindowsChooserPane.getPath());

        mTask.setTargetAny(mAnyCheckedTab.getTabCheckBox().isSelected());
        mTask.setTargetLinux(mLinuxCheckedTab.getTabCheckBox().isSelected());
        mTask.setTargetLinuxAppImage(mTemplateDirAppImageChooserPane.getCheckBox().isSelected());
        mTask.setTargetLinuxSnap(mTemplateDirSnapChooserPane.getCheckBox().isSelected());
        mTask.setTargetMac(mMacCheckedTab.getTabCheckBox().isSelected());
        mTask.setTargetWindows(mWindowsCheckedTab.getTabCheckBox().isSelected());

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

        mScriptPreChooserPane.getCheckBox().setSelected(task.isExecuteScriptPre());
        mScriptPreChooserPane.setPath(task.getScriptPre());
        mScriptPostChooserPane.getCheckBox().setSelected(task.isExecuteScriptPost());
        mScriptPostChooserPane.setPath(task.getScriptPost());

        mTemplateDirAppImageChooserPane.setPath(task.getTemplateDirAppImage());
        mTemplateDirSnapChooserPane.setPath(task.getTemplateDirSnap());
        mResourceChooserPane.setPath(task.getResourceDir());

        mSha256SumCheckBox.setSelected(task.isChecksumSha256());
        mSha512SumCheckBox.setSelected(task.isChecksumSha512());

        mLinuxCheckedTab.getTabCheckBox().setSelected(task.isTargetLinux());
        mJreLinuxChooserPane.setPath(task.getJreLinux());
        mTemplateDirAppImageChooserPane.getCheckBox().setSelected(task.isTargetLinuxAppImage());
        mTemplateDirSnapChooserPane.setSelected(task.isTargetLinuxSnap());

        mMacCheckedTab.getTabCheckBox().setSelected(task.isTargetMac());
        mJreMacChooserPane.setPath(task.getJreMac());

        mWindowsCheckedTab.getTabCheckBox().setSelected(task.isTargetWindows());
        mJreWindowsChooserPane.setPath(task.getJreWindows());

        mAnyCheckedTab.getTabCheckBox().setSelected(task.isTargetAny());

        mNameTextField.requestFocus();
    }

    private void createUI() {
        setHgap(8);

        var nameLabel = new Label(Dict.NAME.toString());
        var descLabel = new Label(Dict.DESCRIPTION.toString());

        mNameTextField = new TextField();
        mDescTextField = new TextField();

        mSha256SumCheckBox = new CheckBox("sha256sum");
        mSha512SumCheckBox = new CheckBox("sha512sum");

        var linuxGridPane = new GridPane(FxHelper.getUIScaled(2), FxHelper.getUIScaled(8));
        linuxGridPane.add(mJreLinuxChooserPane, 0, 0, GridPane.REMAINING, 1);
        linuxGridPane.add(mTemplateDirAppImageChooserPane, 0, 1, 1, 1);
        linuxGridPane.add(mTemplateDirSnapChooserPane, 1, 1, 1, 1);
        FxHelper.autoSizeColumn(linuxGridPane, 2);

        mLinuxCheckedTab = new CheckedTab("Linux", linuxGridPane, null, null);
        var macGridPane = new GridPane(FxHelper.getUIScaled(2), FxHelper.getUIScaled(8));
        macGridPane.addColumn(0, mJreMacChooserPane);
        mMacCheckedTab = new CheckedTab("Mac", macGridPane, null, null);

        var windowsGridPane = new GridPane(FxHelper.getUIScaled(2), FxHelper.getUIScaled(8));
        windowsGridPane.addColumn(0, mJreWindowsChooserPane);
        mWindowsCheckedTab = new CheckedTab("Windows", windowsGridPane, null, null);

        mAnyCheckedTab = new CheckedTab("Any (without JRE)", null, null, null);

        mTabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        mTabPane.getTabs().setAll(mLinuxCheckedTab, mMacCheckedTab, mWindowsCheckedTab, mAnyCheckedTab);

        var checkBoxBox = new HBox(FxHelper.getUIScaled(8),
                mSha256SumCheckBox,
                mSha512SumCheckBox
        );
        checkBoxBox.setAlignment(Pos.BOTTOM_RIGHT);
        checkBoxBox.setPadding(FxHelper.getUIScaledInsets(0, 0, 8, 0));

        int row = 0;
        addRow(row++, nameLabel, descLabel);
        addRow(row++, mNameTextField, mDescTextField);
        add(mSourceChooserPane, 0, ++row, 1, 1);
        add(mDestChooserPane, 1, row, 1, 1);
        add(mScriptPreChooserPane, 0, ++row, 1, 1);
        add(mScriptPostChooserPane, 1, row, 1, 1);
        add(mResourceChooserPane, 0, ++row, 1, 1);
        add(checkBoxBox, 1, row, 1, 1);
        add(mTabPane, 0, ++row, GridPane.REMAINING, 1);

        FxHelper.autoSizeRegionHorizontal(
                mNameTextField,
                mDescTextField,
                mJreLinuxChooserPane,
                mJreMacChooserPane,
                mJreWindowsChooserPane
        );
        mNameTextField.setPrefWidth(1000);
        mDescTextField.setPrefWidth(1000);

        FxHelper.setPadding(FxHelper.getUIScaledInsets(8, 0, 0, 0),
                mSourceChooserPane,
                mDestChooserPane,
                mScriptPreChooserPane,
                mScriptPostChooserPane,
                mTemplateDirAppImageChooserPane,
                mTemplateDirSnapChooserPane,
                mResourceChooserPane
        );

        FxHelper.setPadding(FxHelper.getUIScaledInsets(18, 0, 0, 0),
                mTabPane
        );
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
