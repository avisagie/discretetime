package dt;

import java.awt.BorderLayout;
import java.awt.Dialog.ModalExclusionType;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Popup extends JFrame {
	public interface Listener {
		void done();
	}

	private static final Logger log = Util.getLogger();

	private static final long serialVersionUID = 3761241210357537202L;

	private JPanel contentPane;

	private Notes model;

	private Listener listener;
	private JTextArea textArea;

	private String lastTags = "";

	public void popup() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					setLocationRelativeTo(null);
					textArea.setText(lastTags);
					setVisible(true);
					textArea.grabFocus();
					textArea.selectAll();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void dismiss() {
		setVisible(false);
		Popup.this.listener.done();
	}

	private void commit() {
		try {
			final String note = textArea.getText().trim();
			Popup.this.model.addNote(new Date(), note);
			if (!note.isEmpty()) {
				lastTags = Notes.extractTags(note);
			}
			log.fine("Added note");
		} catch (IOException e1) {
			log.log(Level.WARNING, "Error:", e1);
			JOptionPane.showMessageDialog(Popup.this,
					"Error:" + e1.getMessage(), "Error", ERROR);
		}
		dismiss();
	}

	/**
	 * Create the frame.
	 */
	public Popup(Notes model, Listener listener) {

		this.model = model;
		this.listener = listener;

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				dismiss();
			}
		});

		setIconImage(Toolkit.getDefaultToolkit().getImage(
				Popup.class.getResource("/images/clock32.png")));

		setModalExclusionType(ModalExclusionType.TOOLKIT_EXCLUDE);
		setAlwaysOnTop(true);
		setTitle("'Sup?");
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setBounds(100, 100, 237, 222);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane = new JScrollPane();
		contentPane.add(scrollPane, BorderLayout.CENTER);

		textArea = new JTextArea();
		textArea.setWrapStyleWord(true);
		textArea.setLineWrap(true);
		textArea.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				deal(e);
			}

			private void deal(KeyEvent e) {
				if ((int) e.getKeyChar() == 27) {
					dismiss();
					e.consume();
				} else if ((int) e.getKeyChar() == 10 && e.isAltDown()) {
					textArea.setCaretPosition(textArea.getText().length());
					commit();
					e.consume();
				}
			}
		});
		scrollPane.setViewportView(textArea);

		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.SOUTH);
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		JButton btnDismiss = new JButton("Dismiss");
		btnDismiss.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dismiss();
			}
		});
		panel.add(btnDismiss);

		JButton btnOk = new JButton("Ok");
		btnOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				commit();
			}
		});
		btnOk.setIcon(null);
		panel.add(btnOk);
	}
}
