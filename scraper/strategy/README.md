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

Para adicionar uma nova estratégia:

1. Implemente `SearchEngineScraper` convertendo a `SearchRequest` para o formato
   esperado e produzindo um `ScraperResult`.
2. Registre a implementação na `ScraperFactory` e atualize a enum `Engine`.
3. Opcionalmente exponha uma configuração no `serposcope.conf` e documente o
   novo valor no README.
