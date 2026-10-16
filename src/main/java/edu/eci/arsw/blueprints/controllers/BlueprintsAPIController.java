package edu.eci.arsw.blueprints.controllers;

import edu.eci.arsw.blueprints.model.Blueprint;
import edu.eci.arsw.blueprints.model.Point;
import edu.eci.arsw.blueprints.persistence.BlueprintNotFoundException;
import edu.eci.arsw.blueprints.persistence.BlueprintPersistenceException;
import edu.eci.arsw.blueprints.services.BlueprintsServices;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;


import java.util.Set;

@RestController
@RequestMapping("/api/v1/blueprints")
public class BlueprintsAPIController {

    private final BlueprintsServices services;

    public BlueprintsAPIController(BlueprintsServices services) {
        this.services = services;
    }

    @Operation(summary = "Listar todos los planos")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "OK")
    })

    // GET /blueprints
    @GetMapping
    public ResponseEntity<ApiResponse<Set<Blueprint>>> getAll() {
        return ResponseEntity.ok(new ApiResponse<>(200, "execute ok", services.getAllBlueprints()));
    }

    @Operation(summary = "Planos por autor")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "OK"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "No encontrado")
    })

    // GET /blueprints/{author}
    @GetMapping("/{author}")
    public ResponseEntity<ApiResponse<Set<Blueprint>>> byAuthor(@PathVariable String author)
            throws BlueprintNotFoundException {
        return ResponseEntity.ok(new ApiResponse<>(200, "execute ok", services.getBlueprintsByAuthor(author)));
    }

    @Operation(summary = "Planos por autor y nombre")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "OK"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "No encontrado")
    })

    @GetMapping("/{author}/{bpname}")
    public ResponseEntity<ApiResponse<Blueprint>> byName(@PathVariable String author, @PathVariable String bpname)
            throws BlueprintNotFoundException {
        return ResponseEntity.ok(new ApiResponse<>(200, "execute ok", services.getBlueprint(author, bpname)));
    }

    @Operation(summary = "Crear plano")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Creado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos invalidos")
    })

    // POST /blueprints
    @PostMapping
    public ResponseEntity<ApiResponse<Blueprint>> add(@RequestBody Blueprint bp)
            throws BlueprintPersistenceException {
        if (bp.getAuthor() == null || bp.getAuthor().isBlank()
                || bp.getName() == null || bp.getName().isBlank()) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(400, "author y name son obligatorios", null));
        }
        services.addNewBlueprint(bp);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(201, "created", bp));
    }

    @Operation(summary = "Agregar punto a un plano")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "202", description = "Aceptado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Punto invalido"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "No encontrado"),
    })

    // PUT /blueprints/{author}/{bpname}/points
    @PutMapping("/{author}/{bpname}/points")
    public ResponseEntity<ApiResponse<Blueprint>> addPoint(@PathVariable String author, @PathVariable String bpname,
                                                           @RequestBody Point p)
            throws BlueprintNotFoundException {
        if (p == null) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(400, "punto invalido", null));
        }
        services.addPoint(author, bpname, p.x(), p.y());
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(new ApiResponse<>(202, "accepted", services.getBlueprint(author, bpname)));
    }
}


