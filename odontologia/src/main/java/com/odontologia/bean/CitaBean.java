package com.odontologia.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpSession;

import org.primefaces.context.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.odontologia.model.Cita;
import com.odontologia.model.EstadoCita;
import com.odontologia.model.Odontologo;
import com.odontologia.model.Paciente;
import com.odontologia.model.Persona;
import com.odontologia.service.CitaService;
import com.odontologia.service.EstadoCitaService;
import com.odontologia.service.OdontologoService;
import com.odontologia.service.PacienteService;
import com.odontologia.util.StaticHelp;

@Controller
public class CitaBean {

	@Autowired
	CitaService citaService;

	@Autowired
	PacienteService pacienteService;

	@Autowired
	EstadoCitaService estadoCitaService;

	@Autowired
	OdontologoService odontologoService;

	private Cita cita;
	private List<Cita> citas;
	private List<Cita> citasDePacienteEnEspera;
	private List<Cita> citasDeOdontologoEnEspera;
	private List<Cita> citasDePacienteOdontologo;
	private List<Cita> citasDeOdontologoPaciente;
	private List<Cita> citasPorPaciente;
	private List<Cita> citasPorOdontologo;
	private List<Cita> citasListasHorInic;
	private List<Cita> citasFiltradas; //para el indexRecepcionista
	private EstadoCita estadoCita;
	private List<Cita> citaElegida;
	private List<Cita> elegidos;
	private List<EstadoCita> estadoCitas;
	private List<EstadoCita> estadoCitasSeleccionados;
	
	public CitaBean() {
		cita = new Cita();
		estadoCita = new EstadoCita();
		citas = new ArrayList<>();
		citasDePacienteEnEspera = new ArrayList<>();
		citasDeOdontologoEnEspera = new ArrayList<>();
		citasPorPaciente = new ArrayList<>();
		citasPorOdontologo = new ArrayList<>();
		citasDePacienteOdontologo = new ArrayList<>();
		citasDeOdontologoPaciente = new ArrayList<>();
		citasListasHorInic = new ArrayList<>();
		elegidos=new ArrayList<>();
		estadoCitas = new ArrayList<>();
		citasFiltradas = new ArrayList<>();
		citaElegida = new ArrayList<>();
		estadoCitasSeleccionados = new ArrayList<>();
	}

	public Cita getCita() {
		return cita;
	}

	public void setCita(Cita cita) {
		this.cita = cita;
	}

	public List<Cita> getCitas() {		
		// Para no estar consultando a la BD cada rato al reordenar con "SORTBY"
		//if (citas.size() == 0) {
			citas = citaService.getCitas();
	//	}
		return citas;
	}

	public void setCitas(List<Cita> citas) {
		this.citas = citas;
	}

	public void cancelarCita(ActionEvent actionEvent) {
		// ID DE ESTADO DE CITA = 3 (CANCELADO)
		estadoCita = citaService.estadoCitaFromNombre("CANCELADO");
		cita.setCitaEstadoCita(estadoCita);
		if (citaService.modificarCita(cita)) {
			StaticHelp.correctMessage("Se ha cancelado la cita", "");
			RequestContext.getCurrentInstance().update("frmNuevoo:growl");			
			citaService.enviarEmailCancelarCita(cita.getIdCita());				
			
		}
		cita = new Cita();
		estadoCita = new EstadoCita();

	}
	
	public void cancelarCitaOdontologo(ActionEvent actionEvent) {
		// ID DE ESTADO DE CITA = 3 (CANCELADO)
		estadoCita = citaService.estadoCitaFromNombre("CANCELADO");
		cita.setCitaEstadoCita(estadoCita);
		if (citaService.modificarCita(cita)) {
			StaticHelp.correctMessage("Se ha cancelado la cita", "");
			RequestContext.getCurrentInstance().update("frmNuevoo:growl");			
		    citaService.enviarEmailCancelarCitaOdon(cita.getIdCita());				
			
		}
		cita = new Cita();
		estadoCita = new EstadoCita();

	}

	// Ver las citas(En estado espera) del paciente
	public List<Cita> getCitasDePacienteEnEspera() {
		Persona persona = new Persona();
		Paciente paciente = new Paciente();
		HttpSession session = StaticHelp.getSession();
		persona = (Persona) session.getAttribute("personaSesion");
		paciente = pacienteService.buscarPorPersona(persona);
		// ID DE ESTAOD DE CITA = 1 (EN ESPERA)
		estadoCita = citaService.estadoCitaFromNombre("EN ESPERA");
		citasDePacienteEnEspera = citaService.getCitasPorPacientePorEstado(paciente.getIdPaciente(), estadoCita.getIdEstadoCita());
		return citasDePacienteEnEspera;
	}

	public List<Cita> getCitasDeOdontologoEnEspera() {
		Persona persona = new Persona();
		Odontologo odontologo = new Odontologo();
		HttpSession session = StaticHelp.getSession();
		persona = (Persona) session.getAttribute("personaSesion");
		odontologo = odontologoService.buscarPorPersona(persona);;
		// ID DE ESTAOD DE CITA = 1 (EN ESPERA)
		estadoCita = citaService.estadoCitaFromNombre("EN ESPERA");
		citasDeOdontologoEnEspera = citaService.getCitasPorOdontologoPorEstado(odontologo.getIdOdontologo(), estadoCita.getIdEstadoCita());
		return citasDeOdontologoEnEspera;
	}
	
	// Ver las citas(En estado espera) del paciente
	public List<Cita> getCitasDePacienteOdontologo() {
		Persona persona = new Persona();
		Paciente paciente = new Paciente();
		HttpSession session = StaticHelp.getSession();
		persona = (Persona) session.getAttribute("personaSesion");
		paciente = pacienteService.buscarPorPersona(persona);
		// ID DE ESTAOD DE CITA = 1 (EN ESPERA)
		citasDePacienteOdontologo = citaService.getCitasListaOdontologoSinRepetir(paciente.getIdPaciente(), "EN ESPERA");
		return citasDePacienteOdontologo;
	}

	public void setCitasDePacienteEnEspera(List<Cita> citasDePacienteEnEspera) {
		this.citasDePacienteEnEspera = citasDePacienteEnEspera;
	}

	public EstadoCita getEstadoCita() {
		return estadoCita;
	}

	public void setEstadoCita(EstadoCita estadoCita) {
		this.estadoCita = estadoCita;
	}

	public void setCitasDePacienteOdontologo(List<Cita> citasDePacienteOdontologo) {
		this.citasDePacienteOdontologo = citasDePacienteOdontologo;
	}

	public List<Cita> getCitasPorPaciente() {
		Persona persona = new Persona();
		Paciente paciente = new Paciente();
		HttpSession session = StaticHelp.getSession();
		persona = (Persona) session.getAttribute("personaSesion");
		paciente = pacienteService.buscarPorPersona(persona);
		citasPorPaciente = citaService.getCitasPorPaciente(paciente
				.getIdPaciente());

		return citasPorPaciente;
	}

	public void setCitasPorPaciente(List<Cita> citasPorPaciente) {
		this.citasPorPaciente = citasPorPaciente;
	}

	public List<Cita> getCitasPorOdontologo() {
		Persona persona = new Persona();
		Odontologo odontologo = new Odontologo();
		HttpSession session = StaticHelp.getSession();
		persona = (Persona) session.getAttribute("personaSesion");
		odontologo = odontologoService.buscarPorPersona(persona);
		citasPorOdontologo = citaService.getCitasPorOdontologo(odontologo
				.getIdOdontologo());
		return citasPorOdontologo;
	}

	public void setCitasPorOdontologo(List<Cita> citasPorOdontologo) {
		this.citasPorOdontologo = citasPorOdontologo;
	}
	
	//Para editar el CambiarEstado de Cita
	public void prepararAccion(int idCita){		
		cita = new Cita();
		estadoCita = new EstadoCita();
		cita = citaService.citaFromId(idCita);
		estadoCita = cita.getCitaEstadoCita();	
	}
	
	public void cancelar(ActionEvent actionEvent){
		cita = new Cita();
		estadoCita = new EstadoCita();
	}
	
	public void actualizarCita(ActionEvent actionEvent){
		cita.setCitaEstadoCita(estadoCita);
		if(citaService.modificarCita(cita)){
			StaticHelp.correctMessage(
					"Se ha cambiado de estado a "+estadoCita.getNombre()+"", "");
			RequestContext.getCurrentInstance().update("frmNuevoo:growl");
		}
		cancelar(actionEvent);
	}
	
	
    //Agarrar fecha local
	public TimeZone getTimeZone() {
		TimeZone timeZone = TimeZone.getDefault();
		return timeZone;
	}

	public List<Cita> getCitasDeOdontologoPaciente() {
		Persona persona = new Persona();
		Odontologo odontologo = new Odontologo();
		HttpSession session = StaticHelp.getSession();
		persona = (Persona) session.getAttribute("personaSesion");
		odontologo = odontologoService.buscarPorPersona(persona);
		// ID DE ESTAOD DE CITA = 1 (EN ESPERA)
		citasDeOdontologoPaciente = citaService.getCitasListaPacienteSinRepetir(odontologo.getIdOdontologo(), "EN ESPERA");
		return citasDeOdontologoPaciente;
	}

	public void setCitasDeOdontologoPaciente(
			List<Cita> citasDeOdontologoPaciente) {
		this.citasDeOdontologoPaciente = citasDeOdontologoPaciente;
	}

	public void setCitasDeOdontologoEnEspera(
			List<Cita> citasDeOdontologoEnEspera) {
		this.citasDeOdontologoEnEspera = citasDeOdontologoEnEspera;
	}

	public List<Cita> getCitasListasHorInic() {
		citasListasHorInic = citaService.getCitasList();
		return citasListasHorInic;
	}

	public void setCitasListasHorInic(List<Cita> citasListasHorInic) {
		this.citasListasHorInic = citasListasHorInic;
	}
	
	public List<Cita> getElegidos() {
		return elegidos;
	}

	public void setElegidos(List<Cita> elegidos) {
		this.elegidos = elegidos;
	}
	 
	
	public String enviaremail(){
		for(Cita cita : elegidos){
			citaService.enviarEmail(cita.getCitaPaciente().getPacientePersona().getCorreoElectronico(),cita.getIdCita());
		}
		elegidos = new ArrayList<>();
		return null;
		
	}

	public List<EstadoCita> getEstadoCitas() {
		estadoCitas = estadoCitaService.getEstadoCitas();
		return estadoCitas;
	}

	public void setEstadoCitas(List<EstadoCita> estadoCitas) {
		this.estadoCitas = estadoCitas;
	}

	public List<Cita> getCitasFiltradas() {
		return citasFiltradas;
	}

	public void setCitasFiltradas(List<Cita> citasFiltradas) {
		this.citasFiltradas = citasFiltradas;
	}

	public List<Cita> getCitaElegida() {
		return citaElegida;
	}

	public void setCitaElegida(List<Cita> citaElegida) {
		this.citaElegida = citaElegida;
	}

	public List<EstadoCita> getEstadoCitasSeleccionados() {
		return estadoCitasSeleccionados;
	}

	public void setEstadoCitasSeleccionados(List<EstadoCita> estadoCitasSeleccionados) {
		this.estadoCitasSeleccionados = estadoCitasSeleccionados;
	}
}
