import java.math.BigInteger;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Text;

public class Password_Generator {

	private static final String TAGLINE = "D.K.K.D Certified";
	protected Shell shlAlpgenexe;
	private Text passwordBox;
	private DateTime dateTime;
	private static final BigInteger RSAP = new BigInteger("2872745901979907");
	private static final BigInteger RSAQ = new BigInteger("4189870590687571");
	private static final BigInteger RSAE = new BigInteger("14339983000508380184447786144471");
	private static final BigInteger RSAD = new BigInteger("8362968778254719234079383307851");
	private Encryptor encryptor;

	/**
	 * Launch the application.
	 * @param args
	 */
	private String buildCode(int year, int month, int day, String tagline) {
		StringBuilder sb = new StringBuilder();
		sb.append(year);
		sb.append("-");
		if (month < 10) {
			sb.append("0");
		}
		sb.append(month);
		sb.append("-");
		if (day < 10) {
			sb.append("0");
		}
		sb.append(day);
		sb.append(" " + tagline);
		return sb.toString();
	}
	
	private void setNewPassword() {
		String rawCode = buildCode(dateTime.getYear(), dateTime.getMonth(), dateTime.getDay(), TAGLINE);
		String password = StringUtils.formatPassword(encryptor.encryptMessage(rawCode).get(0));
		passwordBox.setText(password);
		
	}
	
	public static void main(String[] args) {
		try {
			Password_Generator window = new Password_Generator();
			window.encryptor = new Encryptor(RSAE, RSAD, RSAP, RSAQ);
			window.open();
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		while (true) {
			
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents(display);
		shlAlpgenexe.open();
		shlAlpgenexe.layout();

		while (!shlAlpgenexe.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents(Display display) {
		shlAlpgenexe = new Shell(display, SWT.CLOSE | SWT.TITLE | SWT.MIN );
		shlAlpgenexe.setToolTipText("");
		shlAlpgenexe.setSize(450, 335);
		shlAlpgenexe.setText("ALPGen.exe");
		shlAlpgenexe.setLayout(new FormLayout());
		
		dateTime = new DateTime(shlAlpgenexe, SWT.BORDER | SWT.CALENDAR);
		FormData fd_dateTime = new FormData();
		fd_dateTime.left = new FormAttachment(0, 10);
		dateTime.setLayoutData(fd_dateTime);
		
		Button btnGenerate = new Button(shlAlpgenexe, SWT.NONE);
		btnGenerate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setNewPassword();
			}
		});
		fd_dateTime.right = new FormAttachment(100, -98);
		FormData fd_btnGenerate = new FormData();
		fd_btnGenerate.bottom = new FormAttachment(dateTime, 0, SWT.BOTTOM);
		fd_btnGenerate.right = new FormAttachment(100, -10);
		fd_btnGenerate.left = new FormAttachment(dateTime, 6);
		btnGenerate.setLayoutData(fd_btnGenerate);
		btnGenerate.setText("Generate!");
		
		Label lblWelcomeToThe = new Label(shlAlpgenexe, SWT.NONE);
		fd_btnGenerate.top = new FormAttachment(lblWelcomeToThe, 6);
		fd_dateTime.top = new FormAttachment(lblWelcomeToThe, 6);
		FormData fd_lblWelcomeToThe = new FormData();
		fd_lblWelcomeToThe.top = new FormAttachment(0, 10);
		fd_lblWelcomeToThe.left = new FormAttachment(0, 27);
		lblWelcomeToThe.setLayoutData(fd_lblWelcomeToThe);
		lblWelcomeToThe.setText("Welcome to the App Leasing Password Generator!\r\nInstructions:\r\n\t1) Select the date of software expiration in the calendar\r\n\t2) Press the button labeled \"Generate!\"\r\n\t3) Copy the password generated in the textbox below!");
		
		passwordBox = new Text(shlAlpgenexe, SWT.BORDER | SWT.READ_ONLY);
		fd_dateTime.bottom = new FormAttachment(passwordBox, -6);
		passwordBox.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		passwordBox.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		passwordBox.setEditable(false);
		passwordBox.setToolTipText("The password may not reflect the current date selected. Make sure you press Generate!");
		FormData fd_passwordBox = new FormData();
		fd_passwordBox.left = new FormAttachment(0, 10);
		fd_passwordBox.right = new FormAttachment(100, -10);
		fd_passwordBox.top = new FormAttachment(0, 271);
		fd_passwordBox.bottom = new FormAttachment(100, -10);
		passwordBox.setLayoutData(fd_passwordBox);
		

	}
}
