package com.study.automatic.rod.backend.service;

import com.study.automatic.rod.backend.entity.Material;
import com.study.automatic.rod.backend.repository.MaterialRepository;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
//
@Service
public class MaterialService {
    private static final Logger LOGGER = Logger.getLogger(MaterialService.class.getName());
    private final MaterialRepository materialRepository;
//
    public MaterialService(MaterialRepository repository){
        this.materialRepository=repository;
    }

    public List<Material> findAll(){
        return materialRepository.findAll();
    }

    public List<Material> findAll(String stringFilter) {
        if (stringFilter == null || stringFilter.isEmpty()) {
            return materialRepository.findAll();
        } else {
            return materialRepository.search(stringFilter);
        }
    }

    public long count(){
        return materialRepository.count();
    }

    public void delete(Material material){
        materialRepository.delete(material);
    }

    public void save(Material material){
        if (material == null) {
            LOGGER.log(Level.SEVERE,
                    "Material jest wartosci null. Upewnij sie ze wszystko wykonales prawidlowo.");
            return;
        }
        materialRepository.save(material);
    }

    @PostConstruct
    public void populateTestData(){
        if(materialRepository.count() == 0){
            materialRepository.saveAll(
                    Stream.of("Miedź; 0.00017; 200; 20.0",
                            "Mosiądz; 0.00019; 200; 20.0",
                            "Aluminium;0.000231;200;20.0",
                            "Magnez;0.00026;200;20.0",
                            "Złoto;0.00014;200;20.0",
                            "Żelazo;0.000118;200;20.0",
                            "Diament;0.00001;200;20.0",
                            "Ołów;0.00020;200;20.0",
                            "Platyna;0.00009;200;20.0",
                            "Stal; 0.00013; 200; 20.0")
                            .map(line->{
                                String[] split = line.split(";");
                                Material material = new Material();
                                material.setName(split[0].trim());
                                material.setAlpha(Double.parseDouble(split[1]) );
                                material.setLength_0(Double.parseDouble(split[2]) );
                                material.setTemp_0(Double.parseDouble(split[3]) );
                                return material;
                            }).collect(Collectors.toList())
            );//saveAll)
        }//if
    }
}
