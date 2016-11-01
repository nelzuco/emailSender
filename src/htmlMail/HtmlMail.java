package htmlMail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class HtmlMail {
	/**
	* Algunas constantes
	*/
	static public int SIMPLE = 0;
	static public int MULTIPART = 1;
	/**
	* Algunos mensajes de error
	*/
	public static String ERROR_01_LOADFILE = "Error al cargar el fichero";
	public static String ERROR_02_SENDMAIL = "Error al enviar el mail";
	/**
	* Variables
	*/
	private Properties props = new Properties();
	private String host,protocol,user,password;
	private String from,content,to,cc;
	private String subject="";
	/**
	* MultiPart para crear mensajes compuestos
	*/
	MimeMultipart multipart = new MimeMultipart("related");
	// -----
	/**
	* Constructor
	* @param host nombre del servidor de correo
	* @param user usuario de correo
	* @param password password del usuario
	*/
	public HtmlMail(String host,String user,String password)
	{
	props = new Properties();
	props.setProperty("mail.transport.protocol", "smtp");
	//props.setProperty("mail.host", host);
	//props.setProperty("mail.user", user);
	props.setProperty("mail.password", password);
	
	props.put("mail.smtp.host", host);
	props.put("mail.smtp.starttls.enable", "true");
	props.put("mail.smtp.port",587);//25
	props.put("smtp.gmail.com",user);
	props.put("mail.smtp.user", "nelsonzucolillo");
	props.put("mail.smtp.auth", "true"); 
	//session = Session.getDefaultInstance(properties);
	}
	//-----
	/**
	* Muestra un mensaje de trazas
	*
	* @param metodo
	* nombre del metodo
	* @param mensaje
	* mensaje a mostrar
	*/
	static public void trazas(String metodo, String mensaje) {
	// TODO: reemplazar para usar Log4J
	System.out.println("[" + HtmlMail.class.getName() + "][" + metodo
	+ "]:[" + mensaje + "]");
	}
	// -----
	/**
	* Carga el contenido de un fichero de texto HTML en un String
	*
	* @param pathname
	* ruta del fichero
	* @return un String con el contenido del fichero
	* @throws Exception
	* Excepcion levantada en caso de error
	*/	
	static public String loadHTMLFile(String pathname) throws Exception
	{
	String content = "";
	File f = null;
	BufferedReader in = null;
	try
	{
	f = new File(pathname);
	if (f.exists())
	{
	long len_bytes = f.length();
	trazas("loadHTMLFile", "pathname:" + pathname + ", len:"+ len_bytes);
	}
	in = new BufferedReader(new FileReader(f));
	String str;
	while ((str = in.readLine()) != null) {
	// process(str);
	str = str.trim();
	content = content + str;
	}
	in.close();
	return content;
	}
	catch (Exception e)
	{
	String MENSAJE_ERROR = ERROR_01_LOADFILE + ": ['" + pathname + "'] : " + e.toString();
	throw new Exception(MENSAJE_ERROR);
	}
	finally
	{
	if (in != null) in.close();
	}
	}
	
	public static void main(String[] args) throws Exception {
		try {
		// cargar en un string el template del HTML que se va a enviar
		//Suscriptor s= new Suscriptor("nelson zucolillo","nelsonzucolillo@gmail.com" );		
		
		Conector c= new Conector();
		c.conectar(); //conexion a la base de datos
		c.consultar();//consulta la base de datos
		ArrayList lista= c.devolverLista();//lista de suscriptores		
		
		String contenidoHTML = HtmlMail.loadHTMLFile("C:/Users/nzucolillo/Desktop/codigo.html");//archivo html
		//System.out.println("contenido HTML:" + contenidoHTML);
		// propiedades de conexion al servidor de correo:
		HtmlMail mail = new HtmlMail("smtp.gmail.com","nelsonzucolillo@gmail.com","Lifia.,2k16.");
		mail.setFrom("nelsonzucolillo@gmail.com");//DESDE
		mail.setSubject("NOTICIAS"); //ASUNTO
		mail.setCC("");//CON COPIA A:
		// fijar el contenido
		//contenidoHTML=contenidoHTML+"STRING";
		mail.addContent(contenidoHTML);
		// CID de una imagen
		mail.addCID("image", "C:/Users/nzucolillo/Desktop/kiara_1.jpg");		
		// enviar atachados un par de ficheros
		//mail.addAttach("C:/Users/nzucolillo/Desktop/kiara_1.jpg");
		//mail.addAttach("/home/jose/bison_1_0.zip");
		// enviar el correo MULTIPART
		
		for (int i=0; i<lista.size(); i++){
			//System.out.println("email: "+ (((Suscriptor) lista.get(i)).getEmail()));
			mail.setTo((((Suscriptor) lista.get(i)).getEmail()));
			mail.sendMultipart();
		}		
		// para un correo SIMPLE seria:
		// mail.setContent(contenidoHTML);
		// mail.send();
		System.out.println("[ Mail enviado ]");
		} catch (Exception e) {
		e.printStackTrace();
		}
		}
		// ------
		/**
		* Añade el contenido base al multipart
		* @throws Exception Excepcion levantada en caso de error
		*/	
	
	public void addContentToMultipart() throws Exception
		{
		// first part (the html)
		BodyPart messageBodyPart = new MimeBodyPart();
		String htmlText = this.getContent();
		messageBodyPart.setContent(htmlText, "text/html");
		// add it
		this.multipart.addBodyPart(messageBodyPart);
		}
		// -----
		/**
		* Añade el contenido base al multipart
		* @param htmlText contenido html que se muestra en el mensaje de correo
		* @throws Exception Excepcion levantada en caso de error
		*/
		public void addContent(String htmlText) throws Exception
		{
		// first part (the html)
		BodyPart messageBodyPart = new MimeBodyPart();
		messageBodyPart.setContent(htmlText, "text/html");
		// add it
		this.multipart.addBodyPart(messageBodyPart);
		}
		// -----
		/**
		* Añade al mensaje un cid:name utilizado para guardar las imagenes referenciadas en el HTML de la forma <img src=cid:name />
		* @param cidname identificador que se le da a la imagen. suele ser un string generado aleatoriamente.
		* @param pathname ruta del fichero que almacena la imagen
		* @throws Exception excepcion levantada en caso de error
		*/
		public void addCID(String cidname,String pathname) throws Exception
		{
		DataSource fds = new FileDataSource(pathname);
		BodyPart messageBodyPart = new MimeBodyPart();
		messageBodyPart.setDataHandler(new DataHandler(fds));
		messageBodyPart.setHeader("Content-ID","<image>");
		this.multipart.addBodyPart(messageBodyPart);
		}
		// ----
		/**
		* Añade un attachement al mensaje de email
		* @param pathname ruta del fichero
		* @throws Exception excepcion levantada en caso de error
		*/
		public void addAttach(String pathname) throws Exception
		{
		File file = new File(pathname);
		BodyPart messageBodyPart = new MimeBodyPart();
		DataSource ds = new FileDataSource(file);
		messageBodyPart.setDataHandler(new DataHandler(ds));
		messageBodyPart.setFileName(file.getName());
		messageBodyPart.setDisposition(Part.ATTACHMENT);
		this.multipart.addBodyPart(messageBodyPart);
		}
		// ----
		/**
		* Envia un correo multipart
		* @throws Exception Excepcion levantada en caso de error
		*/
		public void sendMultipart() throws Exception
		{
		Session mailSession = Session.getDefaultInstance(this.props);
		mailSession.setDebug(true);
		Transport transport = mailSession.getTransport("smtp");
		MimeMessage message = new MimeMessage(mailSession);
		message.setSubject(this.getSubject());
		message.setFrom(new InternetAddress(this.getFrom()));
		message.addRecipient(Message.RecipientType.TO,new InternetAddress(this.getTo()));
		message.addRecipient(Message.RecipientType.CC,new InternetAddress(this.getCC()));
		// put everything together
		message.setContent(multipart);
		transport.connect((String)props.get("smtp.gmail.com"), "Lifia.,2k16.");
		transport.sendMessage(message,message.getAllRecipients());
		
		transport.close();
		}
		// -----
		/**
		* Envia un correo simple
		* @throws Exception Excepcion levantada en caso de error
		*/
		public void send() throws Exception
		{
		try
		{
		Session mailSession = Session.getDefaultInstance(this.props, null);
		mailSession.setDebug(true);
		Transport transport = mailSession.getTransport();
		MimeMessage message = new MimeMessage(mailSession);
		message.setSubject(this.getSubject());
		message.setFrom(new InternetAddress(this.getFrom()));
		message.setContent(this.getContent(), "text/html");
		message.addRecipient(Message.RecipientType.TO,new InternetAddress(this.getTo()));
		message.addRecipient(Message.RecipientType.CC,new InternetAddress(this.getCC()));
		transport.connect();
		transport.sendMessage(message,message.getAllRecipients());
		transport.close();
		}
		catch(Exception e)
		{
		String MENSAJE_ERROR = ERROR_02_SENDMAIL+" : " + e.toString();
		throw new Exception(MENSAJE_ERROR);
		}
		}
		//-----
		public String getContent() {
		return content;
		}
		public void setContent(String content) {
		this.content = content;
		}
		public String getFrom() {
		return from;
		}
		public void setFrom(String from) {
		this.from = from;
		}
		public String getSubject() {
		return subject;
		}
		public void setSubject(String subject) {
		this.subject = subject;
		}
		public String getTo() {
		return to;
		}
		public void setTo(String to) {
		this.to = to;
		}
		public String getCC() {
			return to;
		}
		public void setCC(String cc) {
			this.cc = cc;
		}
}
		// end of class HTMLMail


