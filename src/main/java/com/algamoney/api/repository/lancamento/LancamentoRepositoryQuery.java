package com.algamoney.api.repository.lancamento;

import com.algamoney.api.repository.filter.LancamentoFilter;
import com.algamoney.api.model.Lancamento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LancamentoRepositoryQuery {

    public Page<Lancamento> filtrar(LancamentoFilter lancamentoFilter, Pageable pageable);

}
