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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import se.trixon.almond.util.Dict;
import se.trixon.nbpackager.ProfileManager;
import se.trixon.nbpackager_core.Profile;

/**
 *
 * @author Patrik Karlström
 */
public class ProfilePanel extends GridPane {

    private TextField mDescTextField;
    private TextField mNameTextField;
    private Button mOkButton;
    private final Profile mProfile;
    private final ProfileManager mProfileManager = ProfileManager.getInstance();

    ProfilePanel(Profile p) {
        mProfile = p;
        createUI();

        mNameTextField.setText(p.getName());

        Platform.runLater(() -> {
            initValidation();
            mNameTextField.requestFocus();
        });
    }

    void save() {
        mProfile.setName(mNameTextField.getText().trim());
        mProfile.setDescription(mDescTextField.getText());
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

        int row = 0;

        add(nameLabel, 0, row, 1, 1);
        add(descLabel, 1, row, 1, 1);
        add(mNameTextField, 0, ++row, 1, 1);
        add(mDescTextField, 1, row, 1, 1);
    }

    private void initValidation() {
        final String text_is_required = "Text is required";
        boolean indicateRequired = false;

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
