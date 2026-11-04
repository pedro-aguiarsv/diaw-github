package com.example.aluguel.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "contratos_credito")
public class ContratoCredito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "pedido_id", unique = true)
    private Pedido pedido;

    @ManyToOne(optional = false)
    @JoinColumn(name = "banco_id")
    private Agente banco;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal valorFinanciado;

    @Column(nullable = false)
    private Integer numeroParcelas;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal taxaMensal;

    @Column(nullable = false)
    private LocalDate concedidoEm;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Pedido getPedido() { return pedido; }
    public void setPedido(Pedido pedido) { this.pedido = pedido; }
    public Agente getBanco() { return banco; }
    public void setBanco(Agente banco) { this.banco = banco; }
    public BigDecimal getValorFinanciado() { return valorFinanciado; }
    public void setValorFinanciado(BigDecimal valorFinanciado) { this.valorFinanciado = valorFinanciado; }
    public Integer getNumeroParcelas() { return numeroParcelas; }
    public void setNumeroParcelas(Integer numeroParcelas) { this.numeroParcelas = numeroParcelas; }
    public BigDecimal getTaxaMensal() { return taxaMensal; }
    public void setTaxaMensal(BigDecimal taxaMensal) { this.taxaMensal = taxaMensal; }
    public LocalDate getConcedidoEm() { return concedidoEm; }
    public void setConcedidoEm(LocalDate concedidoEm) { this.concedidoEm = concedidoEm; }
}
