package org.example.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author pedroRhamon
 */
@RestController
class RequestController {
	
	@Autowired
    private RequestService requestService;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> request(@RequestBody Request request) {
        this.requestService.publisher(request);
        return ResponseEntity.noContent()
                .build();
    }

}
