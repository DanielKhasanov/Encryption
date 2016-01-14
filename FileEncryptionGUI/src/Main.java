import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import java.io.File;
import java.math.BigInteger;
import java.awt.Event;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.EventObject;

import javax.swing.JFileChooser;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.graphics.Point;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.core.databinding.beans.PojoProperties;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.widgets.Composite;

public class Main {
	private DataBindingContext m_bindingContext;

	protected Shell shell;
	private Text fileIn;
	private Text dstFolder;
	private final JFileChooser fileSrc = new JFileChooser();
	private final JFileChooser folderSrc = new JFileChooser();
	private int returnValSrc;
	
	private Button btnChooseDestination;
	private Button btnEncrypt;
	private Button btnDecrypt;
	private Button btnOverwriteExistingFiles;
	private Button btnUseSameKey;
	private Text FormattedDecryptionKey1;
	
	private ScrolledComposite scrolledComposite;
	private ConsoleManager log;
	private Label consoleText;
	private final int LOG_SIZE = 50;
	private Button btnExecute;
	
	private Encryptor recentEncryptor;
	private Decryptor recentDecryptor;
	private String executionDirectory;
	
	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		Display display = Display.getDefault();
		Realm.runWithDefault(SWTObservables.getRealm(display), new Runnable() {
			public void run() {
				try {
					Main window = new Main();
					window.open();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
	
	private void addLogMute(String s) {
		log.add(s);
	}
	private void updateLog() {
		consoleText.setText(log.display());
	}
	private void addLogUpdate(String s) {
		log.add(s);
		updateLog();
	}
	

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setMinimumSize(new Point(450, 347));
		shell.setSize(450, 347);
		shell.setText("F.I.L.Encryptor");
		
		log = new ConsoleManager(LOG_SIZE);
		
		folderSrc.setFileSelectionMode(JFileChooser.FILES_ONLY);

		fileIn = new Text(shell, SWT.BORDER | SWT.READ_ONLY);
		fileIn.setBounds(10, 115, 329, 21);

		dstFolder = new Text(shell, SWT.BORDER | SWT.READ_ONLY);
		dstFolder.setBounds(10, 142, 329, 21);
		
		Button btnChooseFile = new Button(shell, SWT.NONE);
		btnChooseFile.setBounds(345, 113, 79, 25);
		btnChooseFile.setText("Choose File");
		
		btnChooseFile.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(org.eclipse.swt.widgets.Event event) {
				// TODO Auto-generated method stub
				 if (event.widget == btnChooseFile) {
				        int returnVal = fileSrc.showOpenDialog(null);

				        if (returnVal == JFileChooser.APPROVE_OPTION) {
				            File file = fileSrc.getSelectedFile();
				            fileIn.setText(file.getAbsolutePath());
				            //This is where a real application would open the file.
				            //log.append("Opening: " + file.getName() + "." + newline);
				        } else {
				            //log.append("Open command cancelled by user." + newline);
				        }
				   }
				 } 
			}			
		);
		
		btnChooseDestination = new Button(shell, SWT.NONE);
		btnChooseDestination.setBounds(345, 140, 79, 25);
		btnChooseDestination.setText("Destination");
		
		btnOverwriteExistingFiles = new Button(shell, SWT.CHECK);
		btnOverwriteExistingFiles.setToolTipText("Overwrites any existing encrypted files of the same name, otherwise throws an error");
		btnOverwriteExistingFiles.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		btnOverwriteExistingFiles.setBounds(31, 191, 146, 16);
		btnOverwriteExistingFiles.setText("Overwrite Existing Files");
		
		btnUseSameKey = new Button(shell, SWT.CHECK);
		btnUseSameKey.setToolTipText("Only applicable for encryption. If another file was previously encrypted during this session, uses the same key to encrypt. Otherwise generates a new key for encryption. ");
		btnUseSameKey.setBounds(183, 191, 156, 16);
		btnUseSameKey.setText("Use Same Key as Previous");
		
		scrolledComposite = new ScrolledComposite(shell, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setAlwaysShowScrollBars(true);
		scrolledComposite.setBounds(10, 213, 414, 85);
		scrolledComposite.setExpandVertical(true);
		
		Composite composite = new Composite(scrolledComposite, SWT.NONE);
		
		consoleText = new Label(composite, SWT.WRAP);
		consoleText.setBounds(0, 0, 393, 1500);
		consoleText.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
		consoleText.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		scrolledComposite.setContent(composite);
		scrolledComposite.setMinSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		
		addLogUpdate("Debugging Console Status: ONLINE");
		addLogUpdate("You can see error messages and progress notifications here.");
		addLogUpdate(System.getProperty("user.dir"));
		
		FormattedDecryptionKey1 = new Text(shell, SWT.BORDER);
		FormattedDecryptionKey1.setBounds(10, 88, 363, 21);
		
		Button btnCopyToClipboard = new Button(shell, SWT.NONE);
		btnCopyToClipboard.setToolTipText("Copy to Clipboard");
		btnCopyToClipboard.setImage(SWTResourceManager.getImage(Main.class, "/com/sun/javafx/scene/web/skin/Copy_16x16_JFX.png"));
		btnCopyToClipboard.setBounds(377, 88, 47, 21);
		
		btnEncrypt = new Button(shell, SWT.RADIO);
		btnEncrypt.setSelection(true);
		btnEncrypt.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				btnDecrypt.setSelection(false);
				btnEncrypt.setSelection(true);
			}
		});
		
		btnEncrypt.setBounds(31, 169, 90, 16);
		btnEncrypt.setText("Encrypt");
		
		btnDecrypt = new Button(shell, SWT.RADIO);
		btnDecrypt.setSelection(false);
		btnDecrypt.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				btnEncrypt.setSelection(false);
				btnDecrypt.setSelection(true);
			}
		});
		
		btnDecrypt.setText("Decrypt");
		btnDecrypt.setBounds(183, 169, 90, 16);
		
		Label instructionLabel = new Label(shell, SWT.NONE);
		instructionLabel.setBounds(10, 10, 414, 75);
		instructionLabel.setText("Welcome to F.I.L.E, a java based file encryption applet!\r\n   -To begin, please select a file to encrypt or decrypt using Choose File.\r\n   - Next, choose a location and name for the encrypted or decrypted file using Destination.\r\n   - Press Execute to apply encryption or decryption\r\n   - Hover over any options for more information.");
		
		btnExecute = new Button(shell, SWT.NONE);
		btnExecute.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				execute();
			}
		});
		btnExecute.setBounds(345, 171, 79, 36);
		btnExecute.setText("EXECUTE");
		
		btnChooseDestination.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(org.eclipse.swt.widgets.Event event) {
				// TODO Auto-generated method stub
				 if (event.widget == btnChooseDestination) {

				        int returnVal = folderSrc.showSaveDialog(null);

				        if (returnVal == JFileChooser.APPROVE_OPTION) {
				            File file = folderSrc.getSelectedFile();
				            dstFolder.setText(file.getAbsolutePath());
				            //This is where a real application would open the file.
				            //log.append("Opening: " + file.getName() + "." + newline);
				        } else {
				            //log.append("Open command cancelled by user." + newline);
				        }
				   }
				 } 
			}			
		);
		m_bindingContext = initDataBindings();
	}
	
	private void execute() {
		String srcString = fileIn.getText();
		File src = new File(srcString);
		String dstString = dstFolder.getText();
		executionDirectory = System.getProperty("user.dir");
		String executionFileVar = executionDirectory + "/DKKD.tmp";
		
		
		if (!src.exists()) {
			addLogUpdate("Error: the source file does not exist");
			
		//Encryption pathway
		} else if (btnEncrypt.getSelection()) {
			//Need to generate new keys
			if (recentEncryptor == null || !btnUseSameKey.getSelection()) {
				String[] newEDN = Encryptor.newKeyValue().split(" ");

				recentEncryptor = new Encryptor(newEDN[0], newEDN[1], newEDN[2]);				
				if (!dstString.endsWith(".enc")) {
					dstString = dstString + ".enc";	
				}
				File dst = new File(dstString);
				
				if (dst.exists() && !btnOverwriteExistingFiles.getSelection()) {
					addLogUpdate("Error: the destination file already exists");
				} else {
					addLogUpdate("Beginning Encryption...");
					Encryptor tempEncryptor = new Encryptor(newEDN[0], newEDN[1], newEDN[2]);
					Decryptor tempDecryptor = new Decryptor(newEDN[1], newEDN[2]);
					File toDel = new File(dstString);
					if (toDel.exists()) {
						toDel.delete();
					}
					File encryptedFile = tempEncryptor.encryptFile(srcString, dstString);
					if (encryptedFile == null) {
						addLogUpdate(tempEncryptor.getMostRecentError());
						dst.delete();
					} else {
						addLogUpdate("Performing sanity check...");
						File decryptedFile = tempDecryptor.decryptFile(dstString, executionFileVar);
						if (decryptedFile == null) {
							addLogUpdate("Error: the encryption was not clean, this could be a result of a serious machine error");
							dst.delete();
							new File(executionFileVar).delete();
						} else {
							if ("eq".equals(FileComparator.Compare(srcString, executionFileVar))) {
								new File(executionFileVar).delete();
								addLogUpdate("Encryption finished successfully");
								recentEncryptor = tempEncryptor;
								recentDecryptor = tempDecryptor;
								FormattedDecryptionKey1.setText(StringUtils.formatPassword(newEDN[1], newEDN[2]));
								
							}
						}
					}
				}
			//Use the same key as the previous Encryption
			} else {
				if (!dstString.endsWith(".enc")) {
					dstString = dstString + ".enc";	
				}
				File dst = new File(dstString);
				
				if (dst.exists() && !btnOverwriteExistingFiles.getSelection()) {
					addLogUpdate("Error: the destination file already exists");
				} else {
					addLogUpdate("Beginning Encryption...");
					Encryptor tempEncryptor = recentEncryptor;
					Decryptor tempDecryptor = new Decryptor(recentEncryptor.getDecryptionKey(), recentEncryptor.getNModulus());
					File encryptedFile = tempEncryptor.encryptFile(srcString, dstString);
					if (encryptedFile == null) {
						addLogUpdate(tempEncryptor.getMostRecentError());
						dst.delete();
					} else {
						addLogUpdate("Performing sanity check...");
						File decryptedFile = tempDecryptor.decryptFile(dstString, executionFileVar);
						if (decryptedFile == null) {
							addLogUpdate("Error: the encryption was not clean, this could be a result of a serious machine error");
							dst.delete();
							new File(executionFileVar).delete();
						} else {
							if ("eq".equals(FileComparator.Compare(srcString, executionFileVar))) {
								new File(executionFileVar).delete();
								addLogUpdate("Encryption finished successfully");
								recentEncryptor = tempEncryptor;
								recentDecryptor = tempDecryptor;
								FormattedDecryptionKey1.setText(StringUtils.formatPassword(recentEncryptor.getDecryptionKey(), recentEncryptor.getNModulus()));
							}
						}
					}
				}
			}
		//Decryption path
		} else {
			//check the pathways
			if (!srcString.endsWith(".enc")) {
				addLogUpdate("Error: the encypted file must be formatted correctly");
			} else {
					//check overwrite condition
					File dst = new File(dstString);
					
					if (dst.exists() && !btnOverwriteExistingFiles.getSelection()) {
						addLogUpdate("Error: the destination file already exists");
					} else {
						addLogUpdate("Performing sanity check on Key");
						String fullPassword = FormattedDecryptionKey1.getText();
						if (fullPassword.length() < "AAAA-AAAA-1234".length()) {
							addLogUpdate("Error: The key is not valid for decryption");
						} else {
							String decryptionKey = fullPassword.substring(0, 9);
							
							String nModulus = fullPassword.substring(10);
							
							BigInteger d = StringUtils.formatKey(decryptionKey);
							BigInteger n = StringUtils.formatKey(nModulus);
							
							addLogUpdate("Beginning Decryption...");
	
							Decryptor tempDecryptor = new Decryptor(d, n);
							File decryptedFile = tempDecryptor.decryptFile(srcString, dstString);
							if (decryptedFile == null) {
								addLogUpdate("Fatal decryption error. File was not decrypted successfully");
								dst.delete();
							} else {
								addLogUpdate("Decryption complete.");
							}
						}
				}
			}
		}
	}
	
	
	
	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		return bindingContext;
	}
}
