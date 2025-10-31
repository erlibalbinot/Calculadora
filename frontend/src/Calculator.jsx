import React, { useState } from "react";

export default function LoanCalculator() {
  const [form, setForm] = useState({
    initDate: "",
    finishDate: "",
    initPayment: "",
    loan: "",
    percent: "",
  });
  

  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(false);

  const formatCurrency = (value) =>
    value?.toLocaleString("pt-BR", { style: "currency", currency: "BRL" });

  const formataDataBR = (dateString) => {
    if (!dateString) return "";
    const date = new Date(dateString);
    if (isNaN(date)) return dateString;
    return date.toLocaleDateString("pt-BR", { timeZone: "UTC" });
  };

  const handleLoanChange = (e) => {
    let value = e.target.value.replace(/[^\d]/g, ""); 
    if (value) {
      value = (parseInt(value, 10) / 100).toFixed(2); 
      const formatted = value
        .toLocaleString("pt-BR", { style: "currency", currency: "BRL" })
        .replace(".", ",");
      setForm({ ...form, loan: formatted });
    } else {
      setForm({ ...form, loan: "" });
    }
  };

  const handlePercentChange = (e) => {
    let value = e.target.value.replace(",", "."); 
    value = value.replace(/[^\d.]/g, ""); 
    if (value) {
      const num = Math.min(100, Math.max(0, parseFloat(value))); 
      setForm({ ...form, percent: num.toString() });
    } else {
      setForm({ ...form, percent: "" });
    }
  };

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const isValid =
    form.initDate && form.finishDate && form.initPayment && form.loan && form.percent;

  const handleSubmit = async () => {
    setLoading(true);
    try {
      const validate = validatePeriod(form);
      if (validate == "OK") {
        const numericLoan = parseFloat(form.loan.replace(/[R$\s.]/g, "").replace(",", "."));
        const payload = {
          ...form,
          loan: numericLoan,
          percent: parseFloat(form.percent),
        };

        const response = await fetch("http://localhost:8080/api/calculate", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(payload),
        });

        if (!response.ok) throw new Error("Erro ao calcular");

        const data = await response.json();
        setResult(data);
      } else if (validate == "DateInit") {
        alert("Verifique as datas! O primeiro pagamento deve estar entre a data inicial e final.");
      } else {
        alert("Verifique as datas! A data final deve ser maior que a data inicial.");
      }
    } catch (err) {
      alert("Erro ao calcular: " + err.message);
    } finally {
      setLoading(false);
    }
  };

  const validatePeriod = (form) => {
    if (form.finishDate <= form.initDate){
      return "DateFinal";
    }
    if (form.initPayment < form.initDate ||
      form.initPayment > form.finishDate) {
        return "DateInit";
    } 
    return "OK";
  };

  return (
    <div style={styles.container}>
      <h2 style={styles.title}>Calculadora de Empréstimos</h2>
      <div style={{minWidth: "1200px"}}>
        <div style={styles.divInputs}>
          <label style={styles.label}>Data inicial:</label>
          <input
            type="date"
            name="initDate"
            value={form.initDate}
            onChange={handleChange}
            style={styles.input}
          />
        </div>

        <div style={styles.divInputs}>
          <label style={styles.label}>Data final:</label>
          <input
            type="date"
            name="finishDate"
            value={form.finishDate}
            onChange={handleChange}
            style={styles.input}
          />
        </div>

        <div style={styles.divInputs}>
          <label style={styles.label}>Primeiro pagamento:</label>
          <input
            type="date"
            name="initPayment"
            value={form.initPayment}
            onChange={handleChange}
            style={styles.input}
          />
        </div>

        <div style={styles.divInputs}>
          <label style={styles.label}>Valor do empréstimo (R$):</label>
          <input
            type="text"
            name="loan"
            value={form.loan}
            onChange={handleLoanChange}
            style={styles.input}
            placeholder="Ex: 10000"
          />
        </div>

        <div style={styles.divInputs}>
          <label style={styles.label}>Taxa de juros (%):</label>
          <input
            type="number"
            name="percent"
            value={form.percent}
            onChange={handlePercentChange}
            style={styles.input}
            placeholder="Ex: 3%"
          />
        </div>

        <button
          disabled={!isValid || loading}
          onClick={handleSubmit}
          style={{
            ...styles.button,
            backgroundColor: isValid ? "#007bff" : "#ccc",
            cursor: isValid ? "pointer" : "not-allowed",
          }}
        >
          {loading ? "Calculando..." : "Calcular"}
        </button>
      </div>

      {result && result.installmentsData && (
        <div style={{ marginTop: 30 }}>
          <table style={styles.table}>
            <thead style={styles.thead}>
              <tr>
                <th style={styles.th} colspan="3">Empréstimo</th>
                <th style={styles.th}colspan="2">Parcela</th>
                <th style={styles.th}colspan="2">Principal</th>
                <th style={styles.th}colspan="3">Juros</th>
              </tr>
              <tr>
                <th style={styles.th}>Data Competência</th>
                <th style={styles.th}>Valor de empréstimo</th>
                <th style={styles.th}>Saldo Devedor</th>
                <th style={styles.th}>Consolidada</th>
                <th style={styles.th}>Total</th>
                <th style={styles.th}>Amortização</th>
                <th style={styles.th}>Saldo</th>
                <th style={styles.th}>Provisão</th>
                <th style={styles.th}>Acumulado</th>
                <th style={styles.th}>Pago</th>
              </tr>
            </thead>
            <tbody>
              {result.installmentsData.map((item, index) => (
                <tr style={styles.tr} key={index}>
                  <td style={styles.td}>{formataDataBR(item.date)}</td>
                  <td>{formatCurrency(item.loan)}</td>
                  <td>{formatCurrency(item.amountOwed)}</td>
                  <td style={styles.td}>{item.consolidated != null ? item.consolidated + "/" + result.installmentsQtde : ""}</td>
                  <td>{formatCurrency(item.installmentValue)}</td>
                  <td >{formatCurrency(item.amortization)}</td>
                  <td>{formatCurrency(item.balance)}</td>
                  <td>{formatCurrency(item.provision)}</td>
                  <td>{formatCurrency(item.accumulated)}</td>
                  <td>{formatCurrency(item.paid)}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}

const styles = {
  container: {
    maxWidth: 1200,
    margin: "40px auto",
    backgroundColor: "#fff",
    borderRadius: 10,
    boxShadow: "0 2px 8px rgba(0,0,0,0.1)",
    padding: 30,
    fontFamily: "Inter, Arial, sans-serif",
  },
  title: {
    textAlign: "center",
    color: "#333",
    marginBottom: 20,
  },
  label: {
    fontSize: 14,
    fontWeight: 500,
    color: "#444",
    marginBottom: 5,
    marginTop: 10,
    display: "block",
  },
  input: {
    width: "100%",
    padding: "8px 10px",
    border: "1px solid #ccc",
    borderRadius: 6,
    fontSize: 14,
  },
  button: {
    width: "100px",
    padding: 10,
    border: "none",
    borderRadius: 6,
    color: "#fff",
    fontWeight: 600,
    marginTop: 20,
    transition: "background-color 0.3s",
  },
  table: {
    minWidth: "1200px",
    borderCollapse: "collapse",
    marginTop: 15,
  },
  thead: {
    backgroundColor: "#24b1e9ff",
    fontWeight: "bold",
  },
  th: {
    padding: "1px",
    border: "1px solid #ffffffff",
  },
  tr: {
    textAlign: "right",
    padding: "8px",
    border: "1px solid #ddd",
  },
  td: {
    textAlign: "center"
  },
  divInputs: {
    display: "inline-block",
    paddingInline: "2%"
  }
};
