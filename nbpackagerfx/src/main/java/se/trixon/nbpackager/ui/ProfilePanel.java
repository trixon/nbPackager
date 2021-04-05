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
package se.trixon.nbpackager.ui;

import java.util.function.Predicate;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import se.trixon.almond.util.Dict;
import se.trixon.almond.util.fx.FxHelper;
import se.trixon.almond.util.fx.control.FileChooserPane;
import se.trixon.almond.util.fx.control.FileChooserPane.ObjectMode;
import se.trixon.nbpackager.ProfileManager;
import se.trixon.nbpackager_core.Profile;

/**
 *
 * @author Patrik Karlström
 */
public class ProfilePanel extends GridPane {

    private TextField mDescTextField;
    private FileChooserPane mDestChooserPane;
    private FileChooserPane mJreLinuxChooserPane;
    private FileChooserPane mJreMacChooserPane;
    private FileChooserPane mJreWindowsChooserPane;
    private TextField mNameTextField;
    private Button mOkButton;
    private final Profile mProfile;
    private final ProfileManager mProfileManager = ProfileManager.getInstance();
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
    private FileChooserPane mTemplateDirAppImageChooserPane;
    private FileChooserPane mTemplateDirSnapChooserPane;

    ProfilePanel(Profile p) {
        mProfile = p;
        createUI();

        mNameTextField.setText(p.getName());
        mDescTextField.setText(p.getDescription());

        mSourceChooserPane.setPath(p.getSourceDir());
        mDestChooserPane.setPath(p.getDestDir());

        mScriptPreChooserPane.setPath(p.getScriptPre());
        mScriptPostChooserPane.setPath(p.getScriptPost());

        mTemplateDirAppImageChooserPane.setPath(p.getTemplateDirAppImage());
        mTemplateDirSnapChooserPane.setPath(p.getTemplateDirSnap());
        mResourceChooserPane.setPath(p.getResourceDir());

        mJreLinuxChooserPane.setPath(p.getJreLinux());
        mJreMacChooserPane.setPath(p.getJreMac());
        mJreWindowsChooserPane.setPath(p.getJreWindows());

        mTargetLinuxCheckBox.setSelected(p.isTargetLinux());
        mTargetLinuxAppImageCheckBox.setSelected(p.isTargetLinuxAppImage());
        mTargetLinuxSnapCheckBox.setSelected(p.isTargetLinuxSnap());
        mTargetMacCheckBox.setSelected(p.isTargetMac());
        mTargetWindowsCheckBox.setSelected(p.isTargetWindows());
        mTargetAnyCheckBox.setSelected(p.isTargetAny());

        mSha256SumCheckBox.setSelected(p.isChecksumSha256());
        mSha512SumCheckBox.setSelected(p.isChecksumSha512());

        Platform.runLater(() -> {
            initValidation();
            mNameTextField.requestFocus();
        });
    }

    void save() {
        mProfile.setName(mNameTextField.getText().trim());
        mProfile.setDescription(mDescTextField.getText());
        mProfile.setSourceDir(mSourceChooserPane.getPath());
        mProfile.setDestDir(mDestChooserPane.getPath());
        mProfile.setScriptPre(mScriptPreChooserPane.getPath());
        mProfile.setScriptPost(mScriptPostChooserPane.getPath());
        mProfile.setTemplateDirAppImage(mTemplateDirAppImageChooserPane.getPath());
        mProfile.setTemplateDirSnap(mTemplateDirSnapChooserPane.getPath());
        mProfile.setResourceDir(mResourceChooserPane.getPath());

        mProfile.setJreLinux(mJreLinuxChooserPane.getPath());
        mProfile.setJreMac(mJreMacChooserPane.getPath());
        mProfile.setJreWindows(mJreWindowsChooserPane.getPath());

        mProfile.setTargetAny(mTargetAnyCheckBox.isSelected());
        mProfile.setTargetLinux(mTargetLinuxCheckBox.isSelected());
        mProfile.setTargetLinuxAppImage(mTargetLinuxAppImageCheckBox.isSelected());
        mProfile.setTargetLinuxSnap(mTargetLinuxSnapCheckBox.isSelected());
        mProfile.setTargetMac(mTargetMacCheckBox.isSelected());
        mProfile.setTargetWindows(mTargetWindowsCheckBox.isSelected());
    }

    void setOkButton(Button button) {
        mOkButton = button;
    }

    private void createUI() {
        setHgap(8);

        var nameLabel = new Label(Dict.NAME.toString());
        var descLabel = new Label(Dict.DESCRIPTION.toString());

        mNameTextField = new TextField();
        mDescTextField = new TextField();
        mSourceChooserPane = new FileChooserPane(Dict.SELECT.toString(), Dict.SOURCE.toString(), ObjectMode.DIRECTORY, SelectionMode.SINGLE);
        mDestChooserPane = new FileChooserPane(Dict.SELECT.toString(), Dict.DESTINATION.toString(), ObjectMode.DIRECTORY, SelectionMode.SINGLE);
        mScriptPreChooserPane = new FileChooserPane(Dict.SELECT.toString(), "PRE execution script", ObjectMode.FILE, SelectionMode.SINGLE);
        mScriptPostChooserPane = new FileChooserPane(Dict.SELECT.toString(), "POST execution script", ObjectMode.FILE, SelectionMode.SINGLE);
        mTemplateDirAppImageChooserPane = new FileChooserPane(Dict.SELECT.toString(), "AppImage template directory", ObjectMode.DIRECTORY, SelectionMode.SINGLE);
        mTemplateDirSnapChooserPane = new FileChooserPane(Dict.SELECT.toString(), "Snap template directory", ObjectMode.DIRECTORY, SelectionMode.SINGLE);
        mResourceChooserPane = new FileChooserPane(Dict.SELECT.toString(), "Resource base directory", ObjectMode.DIRECTORY, SelectionMode.SINGLE);

        mJreLinuxChooserPane = new FileChooserPane(Dict.SELECT.toString(), "Linux JRE", ObjectMode.DIRECTORY, SelectionMode.SINGLE);
        mJreMacChooserPane = new FileChooserPane(Dict.SELECT.toString(), "Mac JRE", ObjectMode.DIRECTORY, SelectionMode.SINGLE);
        mJreWindowsChooserPane = new FileChooserPane(Dict.SELECT.toString(), "Windows JRE", ObjectMode.DIRECTORY, SelectionMode.SINGLE);

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

        var rowInsets = new Insets(8, 0, 0, 0);
        FxHelper.setPadding(rowInsets,
                mSourceChooserPane,
                mDestChooserPane,
                mScriptPreChooserPane,
                mScriptPostChooserPane,
                mTemplateDirAppImageChooserPane,
                mTemplateDirSnapChooserPane,
                mResourceChooserPane,
                jreGridPane,
                checkBoxBox
        );

        mTargetLinuxAppImageCheckBox.disableProperty().bind(mTargetLinuxCheckBox.selectedProperty().not());
        mTargetLinuxSnapCheckBox.disableProperty().bind(mTargetLinuxCheckBox.selectedProperty().not());
    }

    private void initValidation() {
        final String text_is_required = "Text is required";
        boolean indicateRequired = true;

        Predicate namePredicate = (Predicate) (Object o) -> {
            return mProfileManager.isValid(mProfile.getName(), (String) o);
        };

        var validationSupport = new ValidationSupport();
        validationSupport.registerValidator(mNameTextField, indicateRequired, Validator.createEmptyValidator(text_is_required));
        validationSupport.registerValidator(mNameTextField, indicateRequired, Validator.createPredicateValidator(namePredicate, text_is_required));

        validationSupport.validationResultProperty().addListener((ObservableValue<? extends ValidationResult> observable, ValidationResult oldValue, ValidationResult newValue) -> {
            if (mOkButton != null) {
                mOkButton.setDisable(validationSupport.isInvalid());
            }
        });

        validationSupport.initInitialDecoration();
    }

}
