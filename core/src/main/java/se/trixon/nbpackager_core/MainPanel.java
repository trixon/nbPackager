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

import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import se.trixon.almond.util.Dict;
import se.trixon.almond.util.GraphicsHelper;
import se.trixon.almond.util.Log;
import se.trixon.almond.util.SystemHelper;
import se.trixon.almond.util.icons.material.swing.MaterialIcon;
import static se.trixon.nbpackager_core.Options.*;

/**
 *
 * @author Patrik Karlström
 */
public class MainPanel extends javax.swing.JPanel {

    private final Log mLog = new Log();
    private final Options mOptions = Options.getInstance();
    private static final int ICON_SIZE = 24;
    private Thread mOperationThread;
    private static DialogListener sDialogListener;

    public static DialogListener getDialogListener() {
        return sDialogListener;
    }

    public static void setDialogListener(DialogListener dialogListener) {
        MainPanel.sDialogListener = dialogListener;
    }

    /**
     * Creates new form MainPanel
     */
    public MainPanel() {
        MaterialIcon.setDefaultColor(GraphicsHelper.getBrightness(new JButton().getBackground()) < 128 ? Color.WHITE : Color.BLACK);
        initComponents();
        mLog.setUseTimestamps(false);
        helpButton.setVisible(false);
    }

    public void displayHelp() {
        SystemHelper.desktopBrowse("https://trixon.se/projects/nbpackager");
    }

    public JButton getHelpButton() {
        return helpButton;
    }

    public static int getIconSize() {
        return ICON_SIZE;
    }

    public Log getLog() {
        return mLog;
    }

    private String getProfileName() {
        return profileComboBox.getSelectedItem().toString();
    }

    public void init() {
        setRunState(RunState.STARTABLE);
        profileComboBox.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (!profileComboBox.isPopupVisible() && e != null && e.getKeyCode() == KeyEvent.VK_ENTER) {
                    addButtonActionPerformed(null);
                }
            }
        });

        loadProfiles();
    }

    private void loadProfiles() {
        try {
            String[] profiles = mOptions.getPreferences().node(KEY_PROFILES).childrenNames();
            Arrays.sort(profiles);
            DefaultComboBoxModel<String> defaultComboBoxModel = new DefaultComboBoxModel<>(profiles);
            profileComboBox.setModel(defaultComboBoxModel);
            if (profiles.length > 0) {
                profilePanel.loadProfile(getProfileName());
            } else {
                defaultComboBoxModel.addElement("Default profile");
            }
        } catch (BackingStoreException | NullPointerException ex) {
            Logger.getLogger(MainPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void setRunState(RunState runState) {
        runButton.setVisible(runState == RunState.STARTABLE);
        cancelButton.setVisible(runState == RunState.CANCELABLE);
        progressBar.setIndeterminate(runState == RunState.CANCELABLE);
        dryRunCheckBox.setEnabled(runState == RunState.STARTABLE);
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        toolBar = new javax.swing.JToolBar();
        profileComboBox = new javax.swing.JComboBox<>();
        refreshButton = new javax.swing.JButton();
        addButton = new javax.swing.JButton();
        removeButton = new javax.swing.JButton();
        helpButton = new javax.swing.JButton();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        dryRunCheckBox = new javax.swing.JCheckBox();
        cancelButton = new javax.swing.JButton();
        runButton = new javax.swing.JButton();
        profilePanel = new se.trixon.nbpackager_core.ProfilePanel();
        progressBar = new javax.swing.JProgressBar();

        setLayout(new java.awt.BorderLayout());

        toolBar.setFloatable(false);
        toolBar.setRollover(true);

        profileComboBox.setEditable(true);
        profileComboBox.setMaximumSize(new java.awt.Dimension(256, 32767));
        profileComboBox.setPreferredSize(new java.awt.Dimension(256, 25));
        profileComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                profileComboBoxActionPerformed(evt);
            }
        });
        toolBar.add(profileComboBox);

        refreshButton.setIcon(MaterialIcon._Navigation.REFRESH.getImageIcon(getIconSize()));
        refreshButton.setToolTipText(Dict.REFRESH.toString());
        refreshButton.setFocusable(false);
        refreshButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        refreshButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        refreshButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshButtonActionPerformed(evt);
            }
        });
        toolBar.add(refreshButton);

        addButton.setIcon(MaterialIcon._Content.ADD.getImageIcon(getIconSize()));
        addButton.setToolTipText(Dict.ADD.toString());
        addButton.setFocusable(false);
        addButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        addButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });
        toolBar.add(addButton);

        removeButton.setIcon(MaterialIcon._Content.REMOVE.getImageIcon(getIconSize()));
        removeButton.setToolTipText(Dict.REMOVE.toString());
        removeButton.setFocusable(false);
        removeButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        removeButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        removeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeButtonActionPerformed(evt);
            }
        });
        toolBar.add(removeButton);

        helpButton.setIcon(MaterialIcon._Action.HELP_OUTLINE.getImageIcon(getIconSize()));
        helpButton.setToolTipText(Dict.HELP.toString());
        helpButton.setFocusable(false);
        helpButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        helpButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        helpButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                helpButtonActionPerformed(evt);
            }
        });
        toolBar.add(helpButton);
        toolBar.add(filler1);

        dryRunCheckBox.setText(Dict.DRY_RUN.toString());
        dryRunCheckBox.setFocusable(false);
        dryRunCheckBox.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBar.add(dryRunCheckBox);

        cancelButton.setIcon(MaterialIcon._Navigation.CANCEL.getImageIcon(getIconSize()));
        cancelButton.setFocusable(false);
        cancelButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        cancelButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });
        toolBar.add(cancelButton);

        runButton.setIcon(MaterialIcon._Av.PLAY_ARROW.getImageIcon(getIconSize()));
        runButton.setFocusable(false);
        runButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        runButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        runButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runButtonActionPerformed(evt);
            }
        });
        toolBar.add(runButton);

        add(toolBar, java.awt.BorderLayout.PAGE_START);

        profilePanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 8, 0, 8));
        add(profilePanel, java.awt.BorderLayout.CENTER);
        add(progressBar, java.awt.BorderLayout.PAGE_END);
    }// </editor-fold>//GEN-END:initComponents

    private void profileComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_profileComboBoxActionPerformed
        try {
            profilePanel.loadProfile(getProfileName());
        } catch (NullPointerException e) {
            //nvm
        }
    }//GEN-LAST:event_profileComboBoxActionPerformed

    private void refreshButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshButtonActionPerformed
        profileComboBoxActionPerformed(evt);
    }//GEN-LAST:event_refreshButtonActionPerformed

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        String profileName = (String) profileComboBox.getEditor().getItem();
        DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) profileComboBox.getModel();
        if (model.getIndexOf(profileName) == -1) {
            profileComboBox.addItem(profileName);
            profilePanel.saveProfile(getProfileName());
            loadProfiles();
            profileComboBox.setSelectedItem(profileName);
            //FIXME Does not feel right but it works. Uncomitted editor?
            addButtonActionPerformed(evt);
        }
    }//GEN-LAST:event_addButtonActionPerformed

    private void removeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeButtonActionPerformed
        if (profileComboBox.getSelectedItem() != null) {
            String profileName = getProfileName();
            profileComboBox.removeItem(profileName);
            profilePanel.removeProfile(profileName);
            loadProfiles();
        }
    }//GEN-LAST:event_removeButtonActionPerformed

    private void helpButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_helpButtonActionPerformed
        displayHelp();
    }//GEN-LAST:event_helpButtonActionPerformed

    private void runButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_runButtonActionPerformed
        Profile profile = profilePanel.saveProfile(getProfileName());
        profile.setDryRun(dryRunCheckBox.isSelected());
        mLog.timedOut("Validating settings");

        if (profile.isValid()) {
            mLog.timedOut(profile.toDebugString());
            setRunState(RunState.CANCELABLE);
            mOperationThread = new Thread(() -> {
                Operation operation = new Operation(profile, mLog);
                try {
                    operation.start();
                } catch (IOException ex) {
                    mLog.out(ex.getMessage());
                }
                setRunState(RunState.STARTABLE);
                progressBar.setValue(100);
            });
            mOperationThread.setName("Operation");
            mOperationThread.start();
        } else {
            mLog.timedErr(profile.getValidationError());
        }
    }//GEN-LAST:event_runButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        mOperationThread.interrupt();
        setRunState(RunState.STARTABLE);
        progressBar.setValue(0);
    }//GEN-LAST:event_cancelButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JButton cancelButton;
    private javax.swing.JCheckBox dryRunCheckBox;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JButton helpButton;
    private javax.swing.JComboBox<String> profileComboBox;
    private se.trixon.nbpackager_core.ProfilePanel profilePanel;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JButton refreshButton;
    private javax.swing.JButton removeButton;
    private javax.swing.JButton runButton;
    private javax.swing.JToolBar toolBar;
    // End of variables declaration//GEN-END:variables
}
