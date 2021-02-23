package com.ptc.ptcworker.service

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate
import org.springframework.web.multipart.MultipartFile

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.interfaces.DecodedJWT

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader

@RestController
@RequestMapping('api/worker/v1')
class PtcWorkerController {
	
	@Value('${worker.blob.net}')
	String workerEndPointUrl
	
	static Logger log = LoggerFactory.getLogger(PtcWorkerController.class);
	
	static RestTemplate restTemplate = new RestTemplate()

	@PostMapping('job')
	WorkerResponse submitJob(@RequestHeader('authorization') authorization, @RequestBody WorkerRequest request) {
		
		def response = new WorkerResponse()
		
		DecodedJWT decode = JWT.decode(authorization)
		def tid = decode.claims.tid.asInt()
		def name = decode.claims.name.asString()
		def oid = decode.claims.oid.asInt()
		def aud = decode.claims.aud.asString()
		def azp = decode.claims.azp.asString()
		def email = decode.claims.azp.asString()
		
		def b = Base64.getDecoder().decode(request.getContent().getBytes('UTF-8')) as byte[]
		
		JobWorkerRequest req = new JobWorkerRequest()
		req.setClientId(oid)
		req.setTenentId(tid)
		req.setPayload(request.getContent())
		req.setPayloadSize(b.length as double)
		
		JobWorkerResponse res = restTemplate.postForObject(new URI("${workerEndPointUrl}/api/v1/blob"), req, JobWorkerResponse.class)
		
		response.setClientId(oid)
		response.setTenentId(tid)
		response.setStatusMessage(res.getStatus())
		response.setStatusCode(HttpStatus.OK.value)
		response.setJobId(res.getJobId())
		
		response
	}
	
	/**
	 * @param header
	 * @param jobid
	 * @return
	 */
	@PostMapping('job/{jobid}/status')
	WorkerResponse queryJob(@RequestHeader Map<String, String> header, @PathVariable String jobid) {
		
		log.info "Retrieving Status on job ${jobid}"
		
		JobWorkerResponse res = restTemplate.postForObject(new URI("${workerEndPointUrl}/api/v1/blob/${jobid}"), new JobWorkerRequest(), JobWorkerResponse.class)
		def response = new WorkerResponse()
		response.setStatusMessage(res.getStatus())
		
		if (res.getContent() != null) {
			response.setStatusCode(HttpStatus.OK.value)
		}
		else {
			response.setStatusMessage('NOT_FOUND')
			response.setMessage("${jobid} not found")
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value)
		}
		response.setClientId(res.getClientId())
		response.setTenentId(res.getTenentId())
		response.setJobId(jobid)
		response
	}
}
