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

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import org.apache.commons.lang3.SystemUtils;
import se.trixon.almond.util.Dict;
import static se.trixon.nbpackager_core.Options.*;

/**
 *
 * @author Patrik Karlström
 */
public class ProfilePanel extends javax.swing.JPanel {

    private final Options mOptions = Options.getInstance();

    /**
     * Creates new form ProfilePanel
     */
    public ProfilePanel() {
        initComponents();
        init();
    }

    void loadProfile(String name) {
        Preferences p = mOptions.getPreferences().node(KEY_PROFILES).node(name);

        sourceFileChooserPanel.setPath(p.get(KEY_PROFILE_SOURCE_DIR, ""));
        destFileChooserPanel.setPath(p.get(KEY_PROFILE_DEST_DIR, ""));
        preScriptFileChooserPanel.setPath(p.get(KEY_PROFILE_SCRIPT_PRE, ""));
        postScriptFileChooserPanel.setPath(p.get(KEY_PROFILE_SCRIPT_POST, ""));
        resourcesFileChooserPanel.setPath(p.get(KEY_PROFILE_RESOURCES, ""));
        appImageTemplateFileChooserPanel.setPath(p.get(KEY_PROFILE_APP_IMAGE_TEMPLATE, ""));
        linuxFileChooserPanel.setPath(p.get(KEY_PROFILE_JRE_LINUX, ""));
        macFileChooserPanel.setPath(p.get(KEY_PROFILE_JRE_MAC, ""));
        windowsFileChooserPanel.setPath(p.get(KEY_PROFILE_JRE_WINDOWS, ""));
        appCheckBox.setSelected(p.getBoolean(KEY_PROFILE_TARGET_APP_IMAGE, false));
        linuxCheckBox.setSelected(p.getBoolean(KEY_PROFILE_TARGET_LINUX, false));
        macCheckBox.setSelected(p.getBoolean(KEY_PROFILE_TARGET_MAC, false));
        windowsCheckBox.setSelected(p.getBoolean(KEY_PROFILE_TARGET_WINDOWS, false));
        anyCheckBox.setSelected(p.getBoolean(KEY_PROFILE_TARGET_ANY, false));
        sha256CheckBox.setSelected(p.getBoolean(KEY_PROFILE_CHECKSUM_SHA256, false));
        sha512CheckBox.setSelected(p.getBoolean(KEY_PROFILE_CHECKSUM_SHA512, false));
    }

    void removeProfile(String name) {
        Preferences p = mOptions.getPreferences().node(KEY_PROFILES).node(name);
        try {
            p.removeNode();
        } catch (BackingStoreException ex) {
            Logger.getLogger(ProfilePanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    Profile saveProfile(String name) {
        Preferences p = mOptions.getPreferences().node(KEY_PROFILES).node(name);

        p.put(KEY_PROFILE_SOURCE_DIR, sourceFileChooserPanel.getPath());
        p.put(KEY_PROFILE_DEST_DIR, destFileChooserPanel.getPath());
        p.put(KEY_PROFILE_SCRIPT_PRE, preScriptFileChooserPanel.getPath());
        p.put(KEY_PROFILE_SCRIPT_POST, postScriptFileChooserPanel.getPath());
        p.put(KEY_PROFILE_RESOURCES, resourcesFileChooserPanel.getPath());
        p.put(KEY_PROFILE_APP_IMAGE_TEMPLATE, appImageTemplateFileChooserPanel.getPath());
        p.put(KEY_PROFILE_JRE_LINUX, linuxFileChooserPanel.getPath());
        p.put(KEY_PROFILE_JRE_MAC, macFileChooserPanel.getPath());
        p.put(KEY_PROFILE_JRE_WINDOWS, windowsFileChooserPanel.getPath());
        p.putBoolean(KEY_PROFILE_TARGET_APP_IMAGE, appCheckBox.isSelected());
        p.putBoolean(KEY_PROFILE_TARGET_LINUX, linuxCheckBox.isSelected());
        p.putBoolean(KEY_PROFILE_TARGET_MAC, macCheckBox.isSelected());
        p.putBoolean(KEY_PROFILE_TARGET_WINDOWS, windowsCheckBox.isSelected());
        p.putBoolean(KEY_PROFILE_TARGET_ANY, anyCheckBox.isSelected());
        p.putBoolean(KEY_PROFILE_CHECKSUM_SHA256, sha256CheckBox.isSelected());
        p.putBoolean(KEY_PROFILE_CHECKSUM_SHA512, sha512CheckBox.isSelected());

        Profile profile = new Profile();
        profile.setSource(sourceFileChooserPanel.getFile());
        profile.setDest(destFileChooserPanel.getFile());
        profile.setPreScript(preScriptFileChooserPanel.getFile());
        profile.setPostScript(postScriptFileChooserPanel.getFile());
        profile.setResources(resourcesFileChooserPanel.getFile());
        profile.setAppImageTemplate(appImageTemplateFileChooserPanel.getFile());
        profile.setJreLinux(linuxFileChooserPanel.getFile());
        profile.setJreMac(macFileChooserPanel.getFile());
        profile.setJreWindows(windowsFileChooserPanel.getFile());

        profile.setTargetAppImage(appCheckBox.isSelected());
        profile.setTargetLinux(linuxCheckBox.isSelected());
        profile.setTargetMac(macCheckBox.isSelected());
        profile.setTargetWindows(windowsCheckBox.isSelected());
        profile.setTargetAny(anyCheckBox.isSelected());

        profile.setChecksumSha256(sha256CheckBox.isSelected());
        profile.setChecksumSha512(sha512CheckBox.isSelected());

        return profile;
    }

    private void init() {
        appImageTemplateFileChooserPanel.setEnabled(SystemUtils.IS_OS_LINUX);
        appCheckBox.setEnabled(SystemUtils.IS_OS_LINUX);
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        firstPanel = new javax.swing.JPanel();
        sourceFileChooserPanel = new se.trixon.almond.util.swing.dialogs.FileChooserPanel();
        destFileChooserPanel = new se.trixon.almond.util.swing.dialogs.FileChooserPanel();
        preScriptFileChooserPanel = new se.trixon.almond.util.swing.dialogs.FileChooserPanel();
        postScriptFileChooserPanel = new se.trixon.almond.util.swing.dialogs.FileChooserPanel();
        resourcesFileChooserPanel = new se.trixon.almond.util.swing.dialogs.FileChooserPanel();
        appImageTemplateFileChooserPanel = new se.trixon.almond.util.swing.dialogs.FileChooserPanel();
        jrePanel = new javax.swing.JPanel();
        linuxFileChooserPanel = new se.trixon.almond.util.swing.dialogs.FileChooserPanel();
        macFileChooserPanel = new se.trixon.almond.util.swing.dialogs.FileChooserPanel();
        windowsFileChooserPanel = new se.trixon.almond.util.swing.dialogs.FileChooserPanel();
        destPanel = new javax.swing.JPanel();
        appCheckBox = new javax.swing.JCheckBox();
        linuxCheckBox = new javax.swing.JCheckBox();
        macCheckBox = new javax.swing.JCheckBox();
        windowsCheckBox = new javax.swing.JCheckBox();
        anyCheckBox = new javax.swing.JCheckBox();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(64, 0), new java.awt.Dimension(64, 0), new java.awt.Dimension(64, 32767));
        sha512CheckBox = new javax.swing.JCheckBox();
        sha256CheckBox = new javax.swing.JCheckBox();

        setLayout(new java.awt.GridBagLayout());

        firstPanel.setLayout(new java.awt.GridLayout(3, 2));

        sourceFileChooserPanel.setCheckBoxMode(false);
        sourceFileChooserPanel.setHeader(Dict.SOURCE.toString());
        sourceFileChooserPanel.setMode(1);
        firstPanel.add(sourceFileChooserPanel);

        destFileChooserPanel.setHeader(Dict.DESTINATION.toString());
        destFileChooserPanel.setMode(1);
        firstPanel.add(destFileChooserPanel);

        preScriptFileChooserPanel.setCheckBoxMode(false);
        preScriptFileChooserPanel.setHeader("PRE execution script");
        firstPanel.add(preScriptFileChooserPanel);

        postScriptFileChooserPanel.setHeader("POST execution script");
        firstPanel.add(postScriptFileChooserPanel);

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("se/trixon/cricreator_core/Bundle"); // NOI18N
        resourcesFileChooserPanel.setHeader(bundle.getString("resources")); // NOI18N
        resourcesFileChooserPanel.setMode(1);
        firstPanel.add(resourcesFileChooserPanel);

        appImageTemplateFileChooserPanel.setHeader(bundle.getString("template")); // NOI18N
        appImageTemplateFileChooserPanel.setMode(1);
        firstPanel.add(appImageTemplateFileChooserPanel);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        add(firstPanel, gridBagConstraints);

        jrePanel.setLayout(new java.awt.GridLayout(1, 3));

        linuxFileChooserPanel.setHeader("Linux JRE");
        linuxFileChooserPanel.setMode(1);
        jrePanel.add(linuxFileChooserPanel);

        macFileChooserPanel.setHeader("Mac JRE");
        macFileChooserPanel.setMode(1);
        jrePanel.add(macFileChooserPanel);

        windowsFileChooserPanel.setHeader("Windows JRE");
        windowsFileChooserPanel.setMode(1);
        jrePanel.add(windowsFileChooserPanel);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        add(jrePanel, gridBagConstraints);

        appCheckBox.setText("AppImage");
        destPanel.add(appCheckBox);

        linuxCheckBox.setText("Linux");
        destPanel.add(linuxCheckBox);

        macCheckBox.setText("Mac");
        destPanel.add(macCheckBox);

        windowsCheckBox.setText("Windows");
        destPanel.add(windowsCheckBox);

        anyCheckBox.setText("Any");
        destPanel.add(anyCheckBox);
        destPanel.add(filler1);

        sha512CheckBox.setText("sha512sum");
        destPanel.add(sha512CheckBox);

        sha256CheckBox.setText("sha256sum");
        destPanel.add(sha256CheckBox);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        add(destPanel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox anyCheckBox;
    private javax.swing.JCheckBox appCheckBox;
    private se.trixon.almond.util.swing.dialogs.FileChooserPanel appImageTemplateFileChooserPanel;
    private se.trixon.almond.util.swing.dialogs.FileChooserPanel destFileChooserPanel;
    private javax.swing.JPanel destPanel;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JPanel firstPanel;
    private javax.swing.JPanel jrePanel;
    private javax.swing.JCheckBox linuxCheckBox;
    private se.trixon.almond.util.swing.dialogs.FileChooserPanel linuxFileChooserPanel;
    private javax.swing.JCheckBox macCheckBox;
    private se.trixon.almond.util.swing.dialogs.FileChooserPanel macFileChooserPanel;
    private se.trixon.almond.util.swing.dialogs.FileChooserPanel postScriptFileChooserPanel;
    private se.trixon.almond.util.swing.dialogs.FileChooserPanel preScriptFileChooserPanel;
    private se.trixon.almond.util.swing.dialogs.FileChooserPanel resourcesFileChooserPanel;
    private javax.swing.JCheckBox sha256CheckBox;
    private javax.swing.JCheckBox sha512CheckBox;
    private se.trixon.almond.util.swing.dialogs.FileChooserPanel sourceFileChooserPanel;
    private javax.swing.JCheckBox windowsCheckBox;
    private se.trixon.almond.util.swing.dialogs.FileChooserPanel windowsFileChooserPanel;
    // End of variables declaration//GEN-END:variables
}
