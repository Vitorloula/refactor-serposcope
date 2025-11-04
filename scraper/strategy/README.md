# Scraper Strategy Layer

O pacote `com.serphacker.serposcope.scraper.strategy` implementa o padrão Strategy
para os scrapers do Serposcope. A interface `SearchEngineScraper` define o
contrato para qualquer implementação de coleta de SERP. Adaptadores encapsulam
os scrapers legados (`GoogleScraper`, `FakeGScraper`, `RandomGScraper`) e os
mapeadores (`RequestMapper`, `ResultMapper`) convertem entre os DTOs modernos e
as classes pré-existentes (`GoogleScrapSearch`, `GoogleScrapResult`).

A fábrica `ScraperFactory` escolhe a estratégia com base na enum `Engine`. Use o
módulo `ScraperModule` para fazer o bind do `Engine` padrão via Guice lendo a
propriedade `serposcope.scraper.engine` no arquivo `serposcope.conf`. Valores
suportados: `GOOGLE_HTML`, `GOOGLE_API`, `FAKE`, `RANDOM`.

## Visão geral das alterações

* Criação da interface `SearchEngineScraper` que formaliza o contrato das
  estratégias de scraping e permite a seleção dinâmica em tempo de execução.
* Introdução dos DTOs `SearchRequest` e `ScraperResult`, que transportam
  parâmetros de busca e resultados de forma agnóstica ao scraper legado.
* Implementação de adaptadores (`GoogleHtmlScraperAdapter`, `FakeScraperAdapter`,
  `RandomScraperAdapter` e `GoogleApiScraper`) para reaproveitar os scrapers já
  existentes sem alterar suas regras internas.
* Inclusão de `ScraperFactory`, `Engine` e `ScraperModule` para centralizar a
  escolha da estratégia e integrá-la à configuração existente do projeto.
* Adição do `RankFetchService`, camada de serviço que orquestra a execução da
  estratégia selecionada e expõe uma API coesa para os jobs.
* Escrita de testes unitários para cada adaptador garantindo que o resultado
  corresponde ao fornecido pelo scraper legado.

## Padrões de projeto utilizados

### Strategy
A interface `SearchEngineScraper` define o ponto de variação do algoritmo de
scraping. Cada implementação concreta (`GoogleHtmlScraperAdapter`,
`FakeScraperAdapter`, `RandomScraperAdapter` e `GoogleApiScraper`) fornece uma
estratégia alternativa sem modificar o código cliente. Esse padrão foi escolhido
para permitir a troca de motor de busca ou fornecedor de SERP em tempo de
execução sem ramificações condicionais espalhadas pelo código.

### Adapter
Os adaptadores envolvem os scrapers existentes e convertem os DTOs modernos para
os objetos esperados pelo legado (`GoogleScrapSearch`/`GoogleScrapResult`). O
uso do padrão Adapter minimiza mudanças em classes maduras, mantendo a lógica
anterior intacta enquanto as apresenta através da nova interface unificada.

### Simple Factory + Injeção de Dependência
A classe `ScraperFactory` implementa uma Simple Factory que materializa a
estratégia correta a partir da enum `Engine`, delegando a criação dos scrapers e
centralizando as dependências. O módulo Guice `ScraperModule` lê a configuração e
realiza o binding do `Engine` escolhido, permitindo trocar a estratégia via
configuração sem recompilar o sistema.

### Service Layer
`RankFetchService` fornece um ponto de entrada de alto nível para orquestrar a
execução das estratégias. Esse serviço encapsula a interação com o scraper e o
cliente HTTP, oferecendo uma API estável para os jobs agendados.

### Data Mapper
`RequestMapper` e `ResultMapper` isolam a tradução entre os DTOs modernos e os
modelos legados de scraping. Esse padrão evita que regras de conversão se
espalhem pelas classes cliente, facilitando evoluções futuras dos DTOs.

## Como estender

1. Implemente `SearchEngineScraper` convertendo a `SearchRequest` para o formato
   esperado e produzindo um `ScraperResult`.
2. Registre a implementação na `ScraperFactory` e atualize a enum `Engine`.
3. Opcionalmente exponha uma configuração no `serposcope.conf` e documente o
   novo valor no README.
