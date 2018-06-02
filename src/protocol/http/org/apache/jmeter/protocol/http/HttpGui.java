package org.apache.jmeter.protocol.http;

import org.apache.jmeter.config.gui.AbstractConfigGui;
import org.apache.jmeter.gui.util.HorizontalPanel;
import org.apache.jmeter.gui.util.VerticalPanel;
import org.apache.jmeter.protocol.http.config.gui.UrlConfigGui;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerBase;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerFactory;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.gui.JLabeledTextField;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;

abstract public class HttpGui extends AbstractConfigGui {

	protected static final long serialVersionUID = 241L;

	protected static final Font FONT_DEFAULT = UIManager.getDefaults().getFont("TextField.font");
	protected static final Font FONT_SMALL = new Font("SansSerif", Font.PLAIN, (int) Math.round(FONT_DEFAULT.getSize() * 0.8));

	protected UrlConfigGui urlConfigGui;
	protected JCheckBox retrieveEmbeddedResources;
	protected JCheckBox concurrentDwn;
	protected JTextField concurrentPool;
	protected JCheckBox useMD5;
	protected JLabeledTextField embeddedRE; // regular expression used to match against embedded resource URLs
	protected JTextField sourceIpAddr; // does not apply to Java implementation
	protected JComboBox<String> sourceIpType = new JComboBox<>(HTTPSamplerBase.getSourceTypeList());
	protected JTextField proxyHost;
	protected JTextField proxyPort;
	protected JTextField proxyUser;
	protected JPasswordField proxyPass;
	protected JComboBox<String> httpImplementation = new JComboBox<>(HTTPSamplerFactory.getImplementations());
	protected JTextField connectTimeOut;
	protected JTextField responseTimeOut;

	protected JPanel getTimeOutPanel() {
		JPanel timeOut = new HorizontalPanel();
		timeOut.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
				JMeterUtils.getResString("web_server_timeout_title"))); // $NON-NLS-1$
		final JPanel connPanel = getConnectTimeOutPanel();
		final JPanel reqPanel = getResponseTimeOutPanel();
		timeOut.add(connPanel);
		timeOut.add(reqPanel);
		return timeOut;
	}

	protected JPanel getConnectTimeOutPanel() {
		connectTimeOut = new JTextField(10);

		JLabel label = new JLabel(JMeterUtils.getResString("web_server_timeout_connect")); // $NON-NLS-1$
		label.setLabelFor(connectTimeOut);

		JPanel panel = new JPanel(new BorderLayout(5, 0));
		panel.add(label, BorderLayout.WEST);
		panel.add(connectTimeOut, BorderLayout.CENTER);

		return panel;
	}

	protected JPanel getResponseTimeOutPanel() {
		responseTimeOut = new JTextField(10);

		JLabel label = new JLabel(JMeterUtils.getResString("web_server_timeout_response")); // $NON-NLS-1$
		label.setLabelFor(responseTimeOut);

		JPanel panel = new JPanel(new BorderLayout(5, 0));
		panel.add(label, BorderLayout.WEST);
		panel.add(responseTimeOut, BorderLayout.CENTER);

		return panel;
	}

	protected void enableConcurrentDwn(boolean enable) {
		if (enable) {
			concurrentDwn.setEnabled(true);
			embeddedRE.setEnabled(true);
			if (concurrentDwn.isSelected()) {
				concurrentPool.setEnabled(true);
			}
		} else {
			concurrentDwn.setEnabled(false);
			concurrentPool.setEnabled(false);
			embeddedRE.setEnabled(false);
		}
	}

	protected JPanel createEmbeddedRsrcPanel() {
		// retrieve Embedded resources
		retrieveEmbeddedResources = new JCheckBox(JMeterUtils.getResString("web_testing_retrieve_images")); // $NON-NLS-1$
		// add a listener to activate or not concurrent dwn.
		retrieveEmbeddedResources.addItemListener(e -> {
			if (e.getStateChange() == ItemEvent.SELECTED) { enableConcurrentDwn(true); }
			else { enableConcurrentDwn(false); }
		});
		// Download concurrent resources
		concurrentDwn = new JCheckBox(JMeterUtils.getResString("web_testing_concurrent_download")); // $NON-NLS-1$
		concurrentDwn.addItemListener(e -> {
			if (retrieveEmbeddedResources.isSelected() && e.getStateChange() == ItemEvent.SELECTED) { concurrentPool.setEnabled(true); }
			else { concurrentPool.setEnabled(false); }
		});
		concurrentPool = new JTextField(2); // 2 column size
		concurrentPool.setMinimumSize(new Dimension(10, (int) concurrentPool.getPreferredSize().getHeight()));
		concurrentPool.setMaximumSize(new Dimension(30, (int) concurrentPool.getPreferredSize().getHeight()));

		final JPanel embeddedRsrcPanel = new HorizontalPanel();
		embeddedRsrcPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), JMeterUtils
				.getResString("web_testing_retrieve_title"))); // $NON-NLS-1$
		embeddedRsrcPanel.add(retrieveEmbeddedResources);
		embeddedRsrcPanel.add(concurrentDwn);
		embeddedRsrcPanel.add(concurrentPool);

		// Embedded URL match regex
		embeddedRE = new JLabeledTextField(JMeterUtils.getResString("web_testing_embedded_url_pattern"),20); // $NON-NLS-1$
		embeddedRsrcPanel.add(embeddedRE);

		return embeddedRsrcPanel;
	}

	protected final JPanel getProxyServerPanel(){
		JPanel proxyServer = new HorizontalPanel();
		proxyServer.add(getProxyHostPanel(), BorderLayout.CENTER);
		proxyServer.add(getProxyPortPanel(), BorderLayout.EAST);

		JPanel proxyLogin = new HorizontalPanel();
		proxyLogin.add(getProxyUserPanel());
		proxyLogin.add(getProxyPassPanel());

		JPanel proxyServerPanel = new HorizontalPanel();
		proxyServerPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
				JMeterUtils.getResString("web_proxy_server_title"))); // $NON-NLS-1$
		proxyServerPanel.add(proxyServer);
		proxyServerPanel.add(proxyLogin);

		return proxyServerPanel;
	}

	protected JPanel getProxyHostPanel() {
		proxyHost = new JTextField(10);

		JLabel label = new JLabel(JMeterUtils.getResString("web_server_domain")); // $NON-NLS-1$
		label.setLabelFor(proxyHost);
		label.setFont(FONT_SMALL);

		JPanel panel = new JPanel(new BorderLayout(5, 0));
		panel.add(label, BorderLayout.WEST);
		panel.add(proxyHost, BorderLayout.CENTER);
		return panel;
	}

	protected JPanel getProxyPortPanel() {
		proxyPort = new JTextField(10);

		JLabel label = new JLabel(JMeterUtils.getResString("web_server_port")); // $NON-NLS-1$
		label.setLabelFor(proxyPort);
		label.setFont(FONT_SMALL);

		JPanel panel = new JPanel(new BorderLayout(5, 0));
		panel.add(label, BorderLayout.WEST);
		panel.add(proxyPort, BorderLayout.CENTER);

		return panel;
	}

	protected JPanel getProxyUserPanel() {
		proxyUser = new JTextField(5);

		JLabel label = new JLabel(JMeterUtils.getResString("username")); // $NON-NLS-1$
		label.setLabelFor(proxyUser);
		label.setFont(FONT_SMALL);

		JPanel panel = new JPanel(new BorderLayout(5, 0));
		panel.add(label, BorderLayout.WEST);
		panel.add(proxyUser, BorderLayout.CENTER);
		return panel;
	}

	protected JPanel getProxyPassPanel() {
		proxyPass = new JPasswordField(5);

		JLabel label = new JLabel(JMeterUtils.getResString("password")); // $NON-NLS-1$
		label.setLabelFor(proxyPass);
		label.setFont(FONT_SMALL);

		JPanel panel = new JPanel(new BorderLayout(5, 0));
		panel.add(label, BorderLayout.WEST);
		panel.add(proxyPass, BorderLayout.CENTER);
		return panel;
	}

	/**
	 * Create a panel containing the implementation details
	 *
	 * @return the panel
	 */
	protected final JPanel getImplementationPanel(){
		JPanel implPanel = new HorizontalPanel();
		implPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
				JMeterUtils.getResString("web_server_client"))); // $NON-NLS-1$
		implPanel.add(new JLabel(JMeterUtils.getResString("http_implementation"))); // $NON-NLS-1$
		httpImplementation.addItem("");// $NON-NLS-1$
		implPanel.add(httpImplementation);
		return implPanel;
	}

	protected JPanel createOptionalTasksPanel() {
		// OPTIONAL TASKS
		final JPanel checkBoxPanel = new VerticalPanel();
		checkBoxPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), JMeterUtils
				.getResString("optional_tasks"))); // $NON-NLS-1$

		// Use MD5
		useMD5 = new JCheckBox(JMeterUtils.getResString("response_save_as_md5")); // $NON-NLS-1$
		checkBoxPanel.add(useMD5);

		return checkBoxPanel;
	}

	protected JPanel createSourceAddrPanel() {
		final JPanel sourceAddrPanel = new HorizontalPanel();
		sourceAddrPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), JMeterUtils
				.getResString("web_testing_source_ip"))); // $NON-NLS-1$

		// Add a new field source ip address (for HC implementations only)
		sourceIpType.setSelectedIndex(HTTPSamplerBase.SourceType.HOSTNAME.ordinal()); //default: IP/Hostname
		sourceAddrPanel.add(sourceIpType);

		sourceIpAddr = new JTextField();
		sourceAddrPanel.add(sourceIpAddr);
		return sourceAddrPanel;
	}

}
