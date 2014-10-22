package com.odontologia.service;

import java.util.List;

import com.odontologia.model.Mensaje;

public interface MensajeService {

	public boolean insertarMensaje(Mensaje mensaje);

	public boolean eliminarMensaje(Mensaje mensaje);
	
	public List<Mensaje> getMensajesEmisorReceptor(Integer idPersona);
}