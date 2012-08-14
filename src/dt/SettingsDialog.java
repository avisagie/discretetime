package dt;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;

public class SettingsDialog extends JDialog {
	private static final long serialVersionUID = 3357067363303177353L;

	private static final Logger log = Util.getLogger();
	
	private final JPanel contentPanel = new JPanel();
	private final Settings settings;
	private final JSpinner periodMinutes;
	private final SpinnerNumberModel periodSpinnerModel;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			Settings settings = new Settings(new File("settings.properties"));
			SettingsDialog dialog = new SettingsDialog(settings);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public SettingsDialog(Settings settings) {
		this.settings = settings;		
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		setTitle("discretetime Settings");
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		
		JLabel lblPopupDelay = new JLabel("Popup delay:");
		
		periodMinutes = new JSpinner();
		periodSpinnerModel = new SpinnerNumberModel(settings.getDelayMinutes(), 1, 1440, 1);
		periodMinutes.setModel(periodSpinnerModel);
		
		JLabel lblMinutes = new JLabel("minutes");
		GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
		gl_contentPanel.setHorizontalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblPopupDelay)
					.addGap(18)
					.addComponent(periodMinutes, GroupLayout.PREFERRED_SIZE, 53, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblMinutes)
					.addContainerGap(232, Short.MAX_VALUE))
		);
		gl_contentPanel.setVerticalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblPopupDelay)
						.addComponent(periodMinutes, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblMinutes))
					.addContainerGap(194, Short.MAX_VALUE))
		);
		contentPanel.setLayout(gl_contentPanel);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						applySettings();
						dispose();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}

	private void applySettings() {
		settings.setDelayMinutes(periodSpinnerModel.getNumber().intValue());
		try {
			settings.save();
		} catch (IOException e) {
			log.log(Level.WARNING, "Could not save to " + settings.getFile(), e);
			JOptionPane.showMessageDialog(this, "Could not save settings", "discretetime", ERROR);
		}
	}
}
