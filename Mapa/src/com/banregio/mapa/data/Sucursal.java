package com.banregio.mapa.data;

import com.google.android.maps.GeoPoint;

public class Sucursal {
	
	private int ID;
	private String tipo;
	private String Nombre;
	private String Domicilio;
	private String Horario;
	private String Telefono_Portal;
	private String Telefono_App;
	private Boolean is24hrs;
	private Boolean isOpenSaturday;
	private String Ciudad;
	private String Estado;
	private double latitud;
	private double longitud;
	private String Correo_Gerente;
	private String Correo_Subgerente;
	private String URL_Foto;
	private GeoPoint geoPoint;
	
	public int getID() {
		return ID;
	}
	public void setID(int iD) {
		ID = iD;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public String getNombre() {
		return Nombre;
	}
	public void setNombre(String nombre) {
		Nombre = nombre;
	}
	public String getHorario() {
		return Horario;
	}
	public void setHorario(String horario) {
		Horario = horario;
	}
	public String getDomicilio() {
		return Domicilio;
	}
	public void setDomicilio(String domicilio) {
		Domicilio = domicilio;
	}
	public String getTelefono_Portal() {
		return Telefono_Portal;
	}
	public void setTelefono_Portal(String telefono_Portal) {
		Telefono_Portal = telefono_Portal;
	}
	public String getTelefono_App() {
		return Telefono_App;
	}
	public void setTelefono_App(String telefono_App) {
		Telefono_App = telefono_App;
	}
	public Boolean getIs24hrs() {
		return is24hrs;
	}
	public void setIs24hrs(Boolean is24hrs) {
		this.is24hrs = is24hrs;
	}
	public Boolean getIsOpenSaturday() {
		return isOpenSaturday;
	}
	public void setIsOpenSaturday(Boolean isOpenSaturday) {
		this.isOpenSaturday = isOpenSaturday;
	}
	public String getCiudad() {
		return Ciudad;
	}
	public void setCiudad(String ciudad) {
		Ciudad = ciudad;
	}
	public String getEstado() {
		return Estado;
	}
	public void setEstado(String estado) {
		Estado = estado;
	}
	public double getLongitud() {
		return longitud;
	}
	public void setLongitud(double longitud) {
		this.longitud = longitud;
	}
	public double getLatitud() {
		return latitud;
	}
	public void setLatitud(double latitud) {
		this.latitud = latitud;
	}
	public String getCorreo_Gerente() {
		return Correo_Gerente;
	}
	public void setCorreo_Gerente(String correo_Gerente) {
		Correo_Gerente = correo_Gerente;
	}
	public String getCorreo_Subgerente() {
		return Correo_Subgerente;
	}
	public void setCorreo_Subgerente(String correo_Subgerente) {
		Correo_Subgerente = correo_Subgerente;
	}
	public String getURL_Foto() {
		return URL_Foto;
	}
	public void setURL_Foto(String uRL_Foto) {
		URL_Foto = uRL_Foto;
	}
	public GeoPoint getGeoPoint() {
		return geoPoint;
	}
	public void setGeoPoint(GeoPoint geoPoint) {
		this.geoPoint = geoPoint;
	}
}
