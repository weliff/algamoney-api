package com.example.algamoney.api.repository.lancamento;

import com.example.algamoney.api.model.Lancamento;
import com.example.algamoney.api.repository.filter.LancamentoFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class LancamentoRepositoryImpl implements LancamentoRepositoryQuery {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<Lancamento> filtrar(LancamentoFilter lancamentoFilter, Pageable pageable) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Lancamento> criteriaQuery = builder.createQuery(Lancamento.class);

        Root<Lancamento> root = criteriaQuery.from(Lancamento.class);

        Predicate[] predicates = criarRestricoes(lancamentoFilter, builder, root);
        criteriaQuery.where(predicates);

        TypedQuery<Lancamento> query = entityManager.createQuery(criteriaQuery);

        adicionarRestricoesPaginacao(pageable, query);

        return new PageImpl<Lancamento>(query.getResultList(), pageable, total(lancamentoFilter));
    }

    private long total(LancamentoFilter lancamentoFilter) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = builder.createQuery(Long.class);

        Root<Lancamento> root = criteriaQuery.from(Lancamento.class);

        Predicate[] predicates = criarRestricoes(lancamentoFilter, builder, root);
        criteriaQuery.where(predicates);

        criteriaQuery.select(builder.count(root));

        return entityManager.createQuery(criteriaQuery).getSingleResult();
    }

    private void adicionarRestricoesPaginacao(Pageable pageable, TypedQuery<Lancamento> query) {
        int paginaAtual = pageable.getPageNumber();
        int registrosPorPagina = pageable.getPageSize();

        query.setFirstResult(paginaAtual * registrosPorPagina);
        query.setMaxResults(registrosPorPagina);
    }

    private Predicate[] criarRestricoes(LancamentoFilter lancamentoFilter, CriteriaBuilder builder, Root<Lancamento> root) {
        List<Predicate> predicates = new ArrayList<>();

        if (!StringUtils.isEmpty(lancamentoFilter.getDescricao())) {
            predicates.add(builder.like(
                    builder.lower(root.get("descricao")), "%" + lancamentoFilter.getDescricao().toLowerCase() + "%")
            );
        }

        if (lancamentoFilter.getDataVencimentoDe() != null) {
            predicates.add(
                    builder.greaterThanOrEqualTo(root.get("dataVencimento"), lancamentoFilter.getDataVencimentoDe()));
        }

        if (lancamentoFilter.getDataVencimentoAte() != null) {
            predicates.add(builder.lessThanOrEqualTo(root.get("dataVencimento"), lancamentoFilter.getDataVencimentoAte()));
        }

        return predicates.toArray(new Predicate[predicates.size()]);
    }
}
