package org.springframework.samples.petclinic.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.SOAPConnector;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.webservice.schemas.GetPetsByOwnerRequest;
import com.webservice.schemas.GetPetsByOwnerResponse;
import com.webservice.schemas.Pet;

@WebServlet(urlPatterns = {"/getPetsByOwner"}, loadOnStartup = 1)
public class PetsByOwnerServlet extends HttpServlet {
	Logger log = LoggerFactory.getLogger(PetsByOwnerServlet.class);
	
    private static final String SOAP_SERVICE_URL = "http://localhost:8081/service/petclinic";

	@Autowired
	private SOAPConnector soapConnector;
	
	@Override
	public void init() throws ServletException {
		log.info("Init servlet: {}", this.getClass().getName());
		super.init();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		int ownerId = Integer.valueOf(req.getParameter("ownerId"));

		log.info("Get pets by ownerId: {}", ownerId);
		
		resp.getWriter().println(getPetsByOwner(ownerId));
	}

	private String getPetsByOwner(int ownerId) {
		GetPetsByOwnerRequest request = new GetPetsByOwnerRequest();
		request.setOwnerId(ownerId);
		GetPetsByOwnerResponse response =(GetPetsByOwnerResponse) soapConnector.callWebService(SOAP_SERVICE_URL, request);
		log.info("Got pets by owner {} ========= : ", ownerId);
		
		if (response.getPets() != null && !response.getPets().isEmpty()) {
			for (Pet p : response.getPets()) {
				log.info("Pet : {}", p.getName());
			}
		} else {
			log.info("-- No pets --");
		}
		
		ObjectMapper objectMapper = new ObjectMapper();
		
		try {
			return objectMapper.writeValueAsString(response.getPets());
		} catch (JsonProcessingException e) {
			log.error("Error while converting pets to json.", e);
			return e.getMessage();
		}
	}


}
