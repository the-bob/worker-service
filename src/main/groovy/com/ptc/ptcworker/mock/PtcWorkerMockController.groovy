package com.ptc.ptcworker.mock

import org.springframework.web.bind.annotation.RestController

import com.ptc.ptcworker.service.JobWorkerRequest
import com.ptc.ptcworker.service.JobWorkerResponse

import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping

/**
 * Mock Service
 * @author 
 *
 */
@RestController
@RequestMapping('api/v1')
class PtcWorkerMockController {
	
	static Map<String, JobWorkerRequest> requestMap = new HashMap<>()
	
	def randomizeStatus = { String[] statuses, int n ->
		new Random().with {
			statuses[ nextInt (statuses.length) ]
		}
	}
	
	@PostMapping('blob')
	JobWorkerResponse mockCallJobs(@RequestBody JobWorkerRequest request) {
		UUID uid = UUID.randomUUID()
		JobWorkerResponse res = new JobWorkerResponse()
		requestMap.put(uid.toString(), request)
		res.setStatus('RUNNING')
		res.setJobId(uid.toString())
		res
	}
	
	@PostMapping('blob/{id}')
	JobWorkerResponse mockCallBlob(@PathVariable String id) {
		println "MAP -> ${requestMap}"
		JobWorkerResponse resp = new JobWorkerResponse()
		JobWorkerRequest mockResp = requestMap.get(id)
		if (mockResp) {
			if (mockResp.getStatus() == null || mockResp.getStatus().equals('RUNNING')) {
				def status = randomizeStatus(['RUNNING', 'SUCCESS', 'FAILED'] as String[], 3) as String
				mockResp.setStatus(status)
			}
			if (mockResp.getStatus().equals('SUCCESS')) {
				resp.setContent(mockResp.getPayload())
			}
			println "PAYLOAD ${mockResp.getPayload()}"
			resp.setStatus(mockResp.getStatus())
			resp.setContent(mockResp.getPayload())
			resp.setClientId(mockResp.getClientId())
			resp.setTenentId(mockResp.getTenentId())
		}
		resp.setJobId(id)
		resp
	}
}
