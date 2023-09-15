package com.coursework.Server.Services;

import com.coursework.Server.Model.Batch;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class BatchByIdConverter implements Converter<Long, Batch> {

    @Override
    public Batch convert(Long id) {
        Batch batch = new Batch();
        batch.setBatchId(id);
        return batch;
    }
}
