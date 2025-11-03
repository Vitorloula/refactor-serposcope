# Como Rodar o Serposcope

## 📋 Requisitos

- **Java:** JDK 8 (1.8) ou superior
  - Recomendado: [Amazon Corretto 8](https://docs.aws.amazon.com/corretto/latest/corretto-8-ug/downloads-list.html)
- **Maven:** 3.3+ 
- **Sistema Operacional:** Windows, Linux ou macOS

## 📦 Configuração Inicial

Adicionar dependência no módulo `core/pom.xml`:

```xml
<dependency>
    <groupId>javax.annotation</groupId>
    <artifactId>javax.annotation-api</artifactId>
    <version>1.3.2</version>
</dependency>
```

> **💡 Por quê?** A partir do Java 9, as anotações `javax.annotation` (como `@Generated`) foram removidas do JDK padrão e movidas para um módulo separado. Esta dependência garante compatibilidade com Java 8+.

## 🔨 Compilar

```bash
mvn install -Dmaven.test.skip=true -rf :web
```

## 🚀 Executar

### Modo Desenvolvimento (padrão)
```bash
cd web
export JAVA_HOME="C:\Program Files\Amazon Corretto\jdk1.8.0_472"
export PATH="$JAVA_HOME/bin:$PATH"
mvn ninja:run -Dninja.mainClass=serposcope.lifecycle.Daemon
```

### Modo Produção
```bash
cd web
export JAVA_HOME="C:\Program Files\Amazon Corretto\jdk1.8.0_472"
export PATH="$JAVA_HOME/bin:$PATH"
mvn ninja:run -Dninja.mainClass=serposcope.lifecycle.Daemon -Dninja.mode=prod
```

**Acesso:** http://localhost:7134

## ⚙️ Diferenças entre Modos

| Desenvolvimento | Produção |
|----------------|----------|
| ✅ Dados falsos (RandomGScraper) | ⚠️ Scraping REAL do Google |
| ✅ URLs geradas automaticamente | ⚠️ Requer proxies (recomendado) |
| ✅ Captchas simulados | ⚠️ Requer Captcha Solver |
| ✅ Rápido e seguro | ⚠️ Risco de bloqueio de IP |

> **⚠️ ATENÇÃO:** Em produção, configure proxies em `/admin/proxies` e captcha solver em `/admin/settings`