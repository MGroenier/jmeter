/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.jmeter.protocol.http.control.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ItemEvent;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.UIManager;

import org.apache.jmeter.gui.GUIMenuSortOrder;
import org.apache.jmeter.gui.util.HorizontalPanel;
import org.apache.jmeter.gui.util.VerticalPanel;
import org.apache.jmeter.protocol.http.HttpGui;
import org.apache.jmeter.protocol.http.config.gui.UrlConfigGui;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerBase;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerFactory;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerProxy;
import org.apache.jmeter.samplers.gui.AbstractSamplerGui;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.gui.JLabeledTextField;

/**
 * HTTP Sampler GUI
 */
@GUIMenuSortOrder(1)
public class HttpTestSampleGui extends HttpGui {

    private final boolean isAJP;

    public HttpTestSampleGui() {
        isAJP = false;
        init();
    }

    // For use by AJP
    protected HttpTestSampleGui(boolean ajp) {
        isAJP = ajp;
        init();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void configure(TestElement element) {
        super.configure(element);
        final HTTPSamplerBase samplerBase = (HTTPSamplerBase) element;
        urlConfigGui.configure(element);
        retrieveEmbeddedResources.setSelected(samplerBase.isImageParser());
        concurrentDwn.setSelected(samplerBase.isConcurrentDwn());
        concurrentPool.setText(samplerBase.getConcurrentPool());
        useMD5.setSelected(samplerBase.useMD5());
        embeddedRE.setText(samplerBase.getEmbeddedUrlRE());
        if (!isAJP) {
            sourceIpAddr.setText(samplerBase.getIpSource());
            sourceIpType.setSelectedIndex(samplerBase.getIpSourceType());
            proxyHost.setText(samplerBase.getPropertyAsString(HTTPSamplerBase.PROXYHOST));
            proxyPort.setText(samplerBase.getPropertyAsString(HTTPSamplerBase.PROXYPORT));
            proxyUser.setText(samplerBase.getPropertyAsString(HTTPSamplerBase.PROXYUSER));
            proxyPass.setText(samplerBase.getPropertyAsString(HTTPSamplerBase.PROXYPASS));
            httpImplementation.setSelectedItem(samplerBase.getPropertyAsString(HTTPSamplerBase.IMPLEMENTATION));
            connectTimeOut.setText(samplerBase.getPropertyAsString(HTTPSamplerBase.CONNECT_TIMEOUT));
            responseTimeOut.setText(samplerBase.getPropertyAsString(HTTPSamplerBase.RESPONSE_TIMEOUT));
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public TestElement createTestElement() {
        HTTPSamplerBase sampler = new HTTPSamplerProxy();
        modifyTestElement(sampler);
        return sampler;
    }

    /**
     * Modifies a given TestElement to mirror the data in the gui components.
     * <p>
     * {@inheritDoc}
     */
    @Override
    public void modifyTestElement(TestElement sampler) {
        sampler.clear();
        urlConfigGui.modifyTestElement(sampler);
        final HTTPSamplerBase samplerBase = (HTTPSamplerBase) sampler;
        samplerBase.setImageParser(retrieveEmbeddedResources.isSelected());
        enableConcurrentDwn(retrieveEmbeddedResources.isSelected());
        samplerBase.setConcurrentDwn(concurrentDwn.isSelected());
        samplerBase.setConcurrentPool(concurrentPool.getText());
        samplerBase.setMD5(useMD5.isSelected());
        samplerBase.setEmbeddedUrlRE(embeddedRE.getText());
        if (!isAJP) {
            samplerBase.setIpSource(sourceIpAddr.getText());
            samplerBase.setIpSourceType(sourceIpType.getSelectedIndex());
            samplerBase.setProperty(HTTPSamplerBase.PROXYHOST, proxyHost.getText(),"");
            samplerBase.setProperty(HTTPSamplerBase.PROXYPORT, proxyPort.getText(),"");
            samplerBase.setProperty(HTTPSamplerBase.PROXYUSER, proxyUser.getText(),"");
            samplerBase.setProperty(HTTPSamplerBase.PROXYPASS, String.valueOf(proxyPass.getPassword()),"");
            samplerBase.setProperty(HTTPSamplerBase.IMPLEMENTATION, httpImplementation.getSelectedItem().toString(),"");
            samplerBase.setProperty(HTTPSamplerBase.CONNECT_TIMEOUT, connectTimeOut.getText());
            samplerBase.setProperty(HTTPSamplerBase.RESPONSE_TIMEOUT, responseTimeOut.getText());
        }
        super.configureTestElement(sampler);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getLabelResource() {
        return "web_testing_title"; // $NON-NLS-1$
    }

    private void init() {// called from ctor, so must not be overridable
        setLayout(new BorderLayout(0, 5));
        setBorder(makeBorder());

        // URL CONFIG
        urlConfigGui = new UrlConfigGui(true, true, true);
        
        // HTTP request options
        JPanel httpOptions = new HorizontalPanel();
        httpOptions.add(getImplementationPanel());
        httpOptions.add(getTimeOutPanel());
        // AdvancedPanel (embedded resources, source address and optional tasks)
        JPanel advancedPanel = new VerticalPanel();
        if (!isAJP) {
            advancedPanel.add(httpOptions);
        }
        advancedPanel.add(createEmbeddedRsrcPanel());
        if (!isAJP) {
            advancedPanel.add(createSourceAddrPanel());
            advancedPanel.add(getProxyServerPanel());
        }
        
        advancedPanel.add(createOptionalTasksPanel());
        
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.add(JMeterUtils
                .getResString("web_testing_basic"), urlConfigGui);
        tabbedPane.add(JMeterUtils
                .getResString("web_testing_advanced"), advancedPanel);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, makeTitlePanel(), tabbedPane);
        splitPane.setOneTouchExpandable(true);
        add(splitPane);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Dimension getPreferredSize() {
        return getMinimumSize();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void clearGui() {
        super.clearGui();
        retrieveEmbeddedResources.setSelected(false);
        concurrentDwn.setSelected(false);
        concurrentPool.setText(String.valueOf(HTTPSamplerBase.CONCURRENT_POOL_SIZE));
        enableConcurrentDwn(false);
        useMD5.setSelected(false);
        urlConfigGui.clear();
        embeddedRE.setText(""); // $NON-NLS-1$
        if (!isAJP) {
            sourceIpAddr.setText(""); // $NON-NLS-1$
            sourceIpType.setSelectedIndex(HTTPSamplerBase.SourceType.HOSTNAME.ordinal()); //default: IP/Hostname
            proxyHost.setText(""); // $NON-NLS-1$
            proxyPort.setText(""); // $NON-NLS-1$
            proxyUser.setText(""); // $NON-NLS-1$
            proxyPass.setText(""); // $NON-NLS-1$
            httpImplementation.setSelectedItem(""); // $NON-NLS-1$
            connectTimeOut.setText(""); // $NON-NLS-1$
            responseTimeOut.setText(""); // $NON-NLS-1$
        }
    }

}
