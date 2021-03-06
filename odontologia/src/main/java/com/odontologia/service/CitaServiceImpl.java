package com.odontologia.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.odontologia.model.Cita;
import com.odontologia.model.EstadoCita;
import com.odontologia.model.Odontologo;

@Service
public class CitaServiceImpl implements CitaService {

	@Autowired
	MensajeService emailService;

	@PersistenceContext
	EntityManager em;

	Session mailSession;
	private String fromUser;
	private String fromUserEmailPassword;
	private String emailHost;
	private String cabecera;
	private String cuerpo;
	Properties emailProperties;

	@SuppressWarnings("unchecked")
	@Transactional
	public List<Cita> getCitas() {
		List<Cita> result = new ArrayList<>();
		try {
			Query q = em.createQuery("SELECT c FROM Cita c");
			result = q.getResultList();
		} catch (NoResultException e) {
			System.out.println("ERROR: " + e.getMessage());
		}
		return result;
	}

	@Transactional
	public Cita citaFromId(Integer id) {
		return em.find(Cita.class, id);
	}

	@Transactional
	public boolean insertarCita(Cita cita) {
		boolean resultado = false;
		try {
			em.persist(cita);
			resultado = true;
		} catch (Exception e) {
			System.out.println("ERROR: " + e.getMessage());
			resultado = false;
		}
		return resultado;
	}

	@Transactional
	public boolean modificarCita(Cita cita) {
		boolean resultado = false;
		try {
			em.merge(cita);
			resultado = true;
		} catch (Exception e) {
			System.out.println("ERROR: " + e.getMessage());
			resultado = false;
		}
		return resultado;
	}

	@Transactional
	public boolean eliminarCita(Cita cita) {
		boolean resultado = false;
		try {
			Cita toRemove = em.getReference(Cita.class, cita.getIdCita());
			em.remove(toRemove);
			resultado = true;
		} catch (Exception e) {
			System.out.println("ERROR: " + e.getMessage());
			resultado = false;
		}
		return resultado;
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public List<Cita> getCitasPorPacientePorEstado(Integer idPaciente,
			Integer idEstadoCita) {
		List<Cita> result = new ArrayList<>();
		try {
			// Query q =
			// em.createQuery("SELECT c FROM Cita c JOIN c.citaPaciente cp AND c.citaEstadoCita ce WHERE cp.idPaciente=:idPaciente and ce.idEstadoCita:idEstadoCita");
			// Arreglar m�todo (SIN DISTINCT)
			Query q = em
					.createQuery("SELECT DISTINCT c FROM Cita c,Paciente p, EstadoCita e WHERE c.citaPaciente.idPaciente=:idPaciente AND c.citaEstadoCita.idEstadoCita=:idEstadoCita");
			q.setParameter("idPaciente", idPaciente);
			q.setParameter("idEstadoCita", idEstadoCita);
			result = q.getResultList();
		} catch (NoResultException e) {
			System.out.println("ERROR: " + e.getMessage());
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public List<Cita> getCitasPorOdontologoPorEstado(Integer idOdontologo,
			Integer idEstadoCita) {
		List<Cita> result = new ArrayList<>();
		try {
			Query q = em
					.createQuery("SELECT DISTINCT c FROM Cita c,Odontologo o, EstadoCita e WHERE c.citaOdontologo.idOdontologo=:idOdontologo AND c.citaEstadoCita.idEstadoCita=:idEstadoCita");
			q.setParameter("idOdontologo", idOdontologo);
			q.setParameter("idEstadoCita", idEstadoCita);
			result = q.getResultList();
		} catch (NoResultException e) {
			System.out.println("ERROR: " + e.getMessage());
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public List<Cita> getCitasListaOdontologoSinRepetir(Integer idPaciente,
			String nombreEstadoCita) {
		List<Cita> result = new ArrayList<>();
		try {

			// Arreglar m�todo (SIN DISTINCT)
			Query q = em
					.createQuery("SELECT DISTINCT c FROM Cita c,Paciente p, EstadoCita e JOIN c.citaOdontologo co WHERE c.citaPaciente.idPaciente=:idPaciente AND c.citaEstadoCita.nombre=:nombreEstadoCita group by co.idOdontologo");
			q.setParameter("idPaciente", idPaciente);
			q.setParameter("nombreEstadoCita", nombreEstadoCita);
			result = q.getResultList();
		} catch (NoResultException e) {
			System.out.println("ERROR: " + e.getMessage());
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public List<Cita> getCitasListaPacienteSinRepetir(Integer idOdontologo,
			String nombreEstadoCita) {
		List<Cita> result = new ArrayList<>();
		try {

			// Arreglar m�todo (SIN DISTINCT)
			Query q = em
					.createQuery("SELECT DISTINCT c FROM Cita c,Odontologo  o, EstadoCita e JOIN c.citaPaciente cp WHERE c.citaOdontologo.idOdontologo=:idOdontologo AND c.citaEstadoCita.nombre=:nombreEstadoCita group by cp.idPaciente");
			q.setParameter("idOdontologo", idOdontologo);
			q.setParameter("nombreEstadoCita", nombreEstadoCita);
			result = q.getResultList();
		} catch (NoResultException e) {
			System.out.println("ERROR: " + e.getMessage());
		}
		return result;
	}

	@Transactional
	public EstadoCita estadoCitaFromId(Integer idEstadoCita) {
		return em.find(EstadoCita.class, idEstadoCita);
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public List<Cita> getCitasPorPaciente(Integer idPaciente) {
		List<Cita> result = new ArrayList<>();
		try {
			Query q = em
					.createQuery("SELECT c FROM Cita c JOIN c.citaPaciente cp WHERE cp.idPaciente=:idPaciente");
			q.setParameter("idPaciente", idPaciente);
			result = q.getResultList();
		} catch (NoResultException e) {
			System.out.println("ERROR: " + e.getMessage());
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public List<Cita> getCitasPorOdontologo(Integer idOdontologo) {
		List<Cita> result = new ArrayList<>();
		try {
			Query q = em
					.createQuery("SELECT c FROM Cita c JOIN c.citaOdontologo co WHERE co.idOdontologo=:idOdontologo");
			q.setParameter("idOdontologo", idOdontologo);
			result = q.getResultList();
		} catch (NoResultException e) {
			System.out.println("ERROR: " + e.getMessage());
		}
		return result;
	}

	@Transactional
	public EstadoCita estadoCitaFromNombre(String nombre) {
		EstadoCita estadoCita = new EstadoCita();
		try {
			Query q = em
					.createQuery("SELECT ec FROM EstadoCita ec WHERE ec.nombre=:nombre");
			q.setParameter("nombre", nombre);
			estadoCita = (EstadoCita) q.getSingleResult();
		} catch (NoResultException e) {
			System.out.println("ERROR:" + e.getMessage());

		}
		return estadoCita;

	}

	@SuppressWarnings("unchecked")
	@Transactional
	public List<Cita> getCitasList() {
		List<Cita> result = new ArrayList<>();
		try {
			Query q = em
					.createQuery("SELECT c FROM Cita c WHERE c.horaInicio>=CURRENT_DATE()");
			result = q.getResultList();
		} catch (NoResultException e) {
			System.out.println("ERROR: " + e.getMessage());
		}
		return result;
	}

	@Transactional
	public void enviarEmail(String email, Integer idcita) {
		Cita ci = em.find(Cita.class, idcita);

		String asunto = "Notificacion de cita odontologica de la clinica SPADENT";
		String body = "Se le notifica que su cita odontologica se aproxima : <br/>"
				+ "<br />"
				+ "Codigo de paciente :"
				+ ci.getCitaPaciente().getIdPaciente()
				+ "<br />"
				+ "Sr. o Sra. : "
				+ ci.getCitaPaciente().getPacientePersona().getNombre()
				+ " "
				+ ci.getCitaPaciente().getPacientePersona()
						.getApellidoPaterno()
				+ " "
				+ ci.getCitaPaciente().getPacientePersona()
						.getApellidoMaterno()
				+ "<br />"
				+ "Estado de su cita: "
				+ ci.getCitaEstadoCita().getNombre()
				+ "<br />"
				+ "Fecha y hora de la cita : "
				+ ci.getHoraInicio()
				+ "<br />"
				+ "<br />"
				+ "Agradecemos su preferencia por confiar en clinica odontologica SPADENT"
				+ "<br/>";

		propiedades();
		preparaEnviar(email, asunto, body);
	}

	public void enviarEmailModifCitaPaciente(Cita cita) {

		String email = cita.getCitaPaciente().getPacientePersona()
				.getCorreoElectronico();

		String asunto = "Notificacion de cita odontologica actualizada de la clinica SPADENT";
		String body = "Se le notifica que su cita odontologica ha sido actualizada : <br/>"
				+ "<br />" + "Codigo Paciente :"
				+ cita.getCitaPaciente().getIdPaciente()
				+ "<br />"
				+ "Sr. o Sra. : "
				+ cita.getCitaPaciente().getPacientePersona().getNombre()
				+ " "
				+ cita.getCitaPaciente().getPacientePersona()
						.getApellidoPaterno()
				+ " "
				+ cita.getCitaPaciente().getPacientePersona()
						.getApellidoMaterno()
				+ "<br />"
				+ "Estado de su cita: "
				+ cita.getCitaEstadoCita().getNombre()
				+ "<br />"
				+ "Fecha y hora de la cita : "
				+ cita.getHoraInicio()
				+ "<br />"
				+ "Informacion sobre odontologo"
				+ "<br />"
				+ "Nombre :"
				+ cita.getCitaOdontologo().getOdontologoPersona().getNombre()
				+ " "
				+ cita.getCitaOdontologo().getOdontologoPersona()
						.getApellidoPaterno()
				+ " "
				+ cita.getCitaOdontologo().getOdontologoPersona()
						.getApellidoMaterno()
				+ "<br />"
				+ "Especialidad : "
				+ cita.getCitaOdontologo().getEspecialidad()
				+ "<br />"
				+ "<br />"
				+ "Agradecemos su preferencia por confiar en clinica odontologica SPADENT"
				+ "<br/>";

		propiedades();
		preparaEnviar(email, asunto, body);
	}

	public void enviarEmailModifCitaOdontologo(Odontologo odonto, Cita cita) {

		String email = odonto.getOdontologoPersona().getCorreoElectronico();

		String asunto = "Notificacion de cita odontologica actualizada de la clinica SPADENT";
		String body = "Se le notifica que su cita odontologica ha sido actualizada : <br/>"
				+ "<br />" + "Codigo Odontologo:"
				+ cita.getCitaOdontologo().getIdOdontologo()
				+ "<br />"
				+ "Odontologo : "
				+ cita.getCitaOdontologo().getOdontologoPersona().getNombre()
				+ " "
				+ cita.getCitaOdontologo().getOdontologoPersona()
						.getApellidoPaterno()
				+ " "
				+ cita.getCitaOdontologo().getOdontologoPersona()
						.getApellidoMaterno()
				+ "<br />"
				+ "Estado de su cita: "
				+ cita.getCitaEstadoCita().getNombre()
				+ "<br />"
				+ "Fecha y hora de la cita : "
				+ cita.getHoraInicio()
				+ "<br />"
				+ "Informacion sobre el Paciente :"
				+ "<br />"
				+ "Nombre :"
				+ cita.getCitaPaciente().getPacientePersona().getNombre()
				+ " "
				+ cita.getCitaPaciente().getPacientePersona()
						.getApellidoPaterno()
				+ " "
				+ cita.getCitaPaciente().getPacientePersona()
						.getApellidoMaterno()
				+ "<br />"
				+ "Telefono :"
				+ cita.getCitaPaciente().getPacientePersona().getCelular()
				+ "<br />"
				+ "<br />"
				+ "Agradecemos su preferencia por confiar en clinica odontologica SPADENT"
				+ "<br/>";

		propiedades();
		preparaEnviar(email, asunto, body);
	}

	public void enviarEmailRegistCitaPaciente(Integer idcita) {
		Cita cita = em.find(Cita.class, idcita);
		String email = cita.getCitaPaciente().getPacientePersona()
				.getCorreoElectronico();

		String asunto = "Notificacion de reserva de cita odontologica de la clinica SPADENT";
		String body = "Se le notifica que su cita odontologica ha sido registrado : <br/>"
				+ "<br />" + "Codigo Paciente :"
				+ cita.getCitaPaciente().getIdPaciente()
				+ "<br />"
				+ "Sr. o Sra. : "
				+ cita.getCitaPaciente().getPacientePersona().getNombre()
				+ " "
				+ cita.getCitaPaciente().getPacientePersona()
						.getApellidoPaterno()
				+ " "
				+ cita.getCitaPaciente().getPacientePersona()
						.getApellidoMaterno()
				+ "<br />"
				+ "Estado de su cita: "
				+ cita.getCitaEstadoCita().getNombre()
				+ "<br />"
				+ "Fecha y hora de la cita : "
				+ cita.getHoraInicio()
				+ "<br />"
				+ "Informacion sobre odontologo"
				+ "<br />"
				+ "Nombre :"
				+ cita.getCitaOdontologo().getOdontologoPersona().getNombre()
				+ " "
				+ cita.getCitaOdontologo().getOdontologoPersona()
						.getApellidoPaterno()
				+ " "
				+ cita.getCitaOdontologo().getOdontologoPersona()
						.getApellidoMaterno()
				+ "<br />"
				+ "Especialidad : "
				+ cita.getCitaOdontologo().getEspecialidad()
				+ "<br />"
				+ "<br />"
				+ "Agradecemos su preferencia por confiar en clinica odontologica SPADENT"
				+ "<br/>";

		propiedades();
		preparaEnviar(email, asunto, body);
	}

	@Transactional
	public void enviarEmailCancelarCita(Integer idcita) {
		Cita ci = em.find(Cita.class, idcita);

		String email = "odontologospadent@gmail.com";
		String asunto = "Notificacion de cita cancelada";
		String body = "Se notifica que el paciente : <br/>"
				+ "<br />"
				+ "Codigo de paciente :"
				+ ci.getCitaPaciente().getIdPaciente()
				+ "<br />"
				+ "Sr. o Sra. : "
				+ ci.getCitaPaciente().getPacientePersona().getNombre()
				+ " "
				+ ci.getCitaPaciente().getPacientePersona()
						.getApellidoPaterno()
				+ " "
				+ ci.getCitaPaciente().getPacientePersona()
						.getApellidoMaterno() + "<br />" + "Telefono :"
				+ ci.getCitaPaciente().getPacientePersona().getCelular()
				+ "<br />" + "Estado de su cita : "
				+ ci.getCitaEstadoCita().getNombre() + "<br />"
				+ "Fecha y hora de la cita : " + ci.getHoraInicio() + "<br />"
				+ "<br />";

		propiedades();
		preparaEnviar(email, asunto, body);
	}

	public void enviarEmailCancelarCitaOdon(Integer idcita) {
		Cita ci = em.find(Cita.class, idcita);

		String email = "odontologospadent@gmail.com";
		String asunto = "Notificacion de cita cancelada";
		String body = "Se notifica que el odontologo : <br/>"
				+ "<br />"
				+ "Codigo Odontologo:"
				+ ci.getCitaOdontologo().getIdOdontologo()
				+ "<br />"
				+ "Odontologo : "
				+ ci.getCitaOdontologo().getOdontologoPersona().getNombre()
				+ " "
				+ ci.getCitaOdontologo().getOdontologoPersona()
						.getApellidoPaterno()
				+ " "
				+ ci.getCitaOdontologo().getOdontologoPersona()
						.getApellidoMaterno() + "<br />" + "Especialidad : "
				+ ci.getCitaOdontologo().getEspecialidad() + "<br />"
				+ "Telefono :"
				+ ci.getCitaOdontologo().getOdontologoPersona().getCelular()
				+ "<br />" + "Estado de su cita: "
				+ ci.getCitaEstadoCita().getNombre() + "<br />"
				+ "Fecha y hora de la cita : " + ci.getHoraInicio() + "<br />";

		propiedades();
		preparaEnviar(email, asunto, body);
	}

	public void propiedades() {
		emailProperties = System.getProperties();
		emailProperties.put("mail.smtp.port", "587");
		emailProperties.put("mail.smtp.auth", "true");
		emailProperties.put("mail.smtp.starttls.enable", "true");
		mailSession = Session.getDefaultInstance(emailProperties, null);
		setFromUser("odontologospadent@gmail.com");
		setFromUserEmailPassword("tp2014jp");
		setEmailHost("smtp.gmail.com");
	}

	public String getFromUser() {
		return fromUser;
	}

	public void setFromUser(String fromUser) {
		this.fromUser = fromUser;
	}

	public String getFromUserEmailPassword() {
		return fromUserEmailPassword;
	}

	public void setFromUserEmailPassword(String fromUserEmailPassword) {
		this.fromUserEmailPassword = fromUserEmailPassword;
	}

	public String getEmailHost() {
		return emailHost;
	}

	public void setEmailHost(String emailHost) {
		this.emailHost = emailHost;
	}

	public String getCabecera() {
		return cabecera;
	}

	public void setCabecera(String cabecera) {
		this.cabecera = cabecera;
	}

	public String getCuerpo() {
		return cuerpo;
	}

	public void setCuerpo(String cuerpo) {
		this.cuerpo = cuerpo;
	}

	public void preparaEnviar(String para, String cabecera, String cuerpo) {
		try {
			MimeMessage emailMessage = new MimeMessage(mailSession);
			emailMessage.addRecipient(Message.RecipientType.TO,
					new InternetAddress(para));
			emailMessage.setSubject(cabecera);
			emailMessage.setContent(cuerpo, "text/html");
			Transport transport = mailSession.getTransport("smtp");
			transport.connect(getEmailHost(), getFromUser(),
					getFromUserEmailPassword());
			transport
					.sendMessage(emailMessage, emailMessage.getAllRecipients());
			transport.close();
			System.out.println("Email sent successfully.");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}

	@Transactional
	public boolean validarHorarioCita(Cita cita) {
		boolean esRepetido = false;
		Cita citaT = new Cita();
		try {
			Query q = em.createQuery("SELECT c FROM Cita c WHERE c.horaInicio=:citahoraInicio and c.citaOdontologo.idOdontologo=:citacitaOdontologoidOdontologo and c.citaEstadoCita.nombre='EN ESPERA'");
			q.setParameter("citahoraInicio", cita.getHoraInicio());
			q.setParameter("citacitaOdontologoidOdontologo", cita.getCitaOdontologo().getIdOdontologo());
			citaT = (Cita) q.getSingleResult();
			if(citaT.getIdCita()!=null){
				esRepetido = true;
			}
		} catch (NoResultException e) {
			System.out.println("ERROR: " + e.getMessage());
		}
		return esRepetido;
	}

}
