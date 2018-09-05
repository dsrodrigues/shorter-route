# shorter-route
### Desafio Empresa X
A Empresa X esta desenvolvendo um novo sistema de logística e sua ajuda é muito importante neste momento. Sua tarefa será desenvolver o novo sistema de entregas visando sempre o menor custo. Para popular sua base de dados  o sistema precisa expor um Webservices que aceite o formato de malha logística (exemplo abaixo), nesta mesma  requisição o requisitante deverá informar um nome para este mapa. É importante que os mapas sejam persistidos  para evitar que a cada novo deploy todas as informações desapareçam. O formato de malha logística é bastante simples, cada linha mostra uma rota: ponto de origem, ponto de destino e distância entre os pontos em quilômetros.

A B 10

B D 15

A C 20

C D 30

B E 50

D E 30

Com os mapas carregados o requisitante irá procurar o menor valor de entrega e seu caminho, para isso ele passará o mapa, nome do ponto de origem, nome do ponto de destino, autonomia do caminhão (km/l) e o valor do litro do combustível, agora sua tarefa é criar este Webservices. Um exemplo de entrada seria mapa SP, origem A, destino D, autonomia 10, valor do litro 2,50; a resposta seria a rota A B D com custo de 6,25.

### Pré-Requisitos
* [Donwload, instalar e Iniciar Neo4j Server].
* Acessar e alterar a senha [http://localhost:7474].
* Configurar os parametros no arquivo neo4j-config.properties
* Iniciar o Projeto com Java 8/Tomcat 8

### Utilizando o serviço
**Exemplo de requisição:**

- **POST** [http://localhost:8080/shorter-route/route/createMap](http://localhost:8080/shorter-route/route/createMap)
- **Accept:** application/json
- **Content-Type:** application/json

```html
	{
		"name": "SP",
		"routes": [
			{ "distance": 10, "origin": "A", "destiny": "B" },
			{ "distance": 15, "origin": "B", "destiny": "D" },
			{ "distance": 20, "origin": "A", "destiny": "C" },
			{ "distance": 30, "origin": "C", "destiny": "D" },
			{ "distance": 50, "origin": "B", "destiny": "E" },
			{ "distance": 30, "origin": "D", "destiny": "E" }
		]
	}
```

**Exemplo de resposta:**

- **201** CREATED

### Encontrar o menor caminho

**Exemplo de requisição:**

- **GET** [http://localhost:8080/shorter-route/route/findShorterRoute?origin=A&destiny=D&autonomy=10&fuelPrice=2.5](http://localhost:8080/shorter-route/route/findShorterRoute?origin=A&destiny=D&autonomy=10&fuelPrice=2.5)
- **Accept:** application/json
- **Content-Type:** application/json

**Exemplo de resposta:**

- **200** OK

```html
	{
		"distance": 25,
		"cost": 6.25,
		"directions": [
			"A",
			"B",
			"D"
		]
	}
```

## Códigos de erro

| Código | Descrição   | Motivo                                       |
| ------ | ----------- | -------------------------------------------- |
| 400    | Bad Request | Parâmetros ausentes ou parâmetros inválidos. |
| 404    | Not Found   | Valor não encontrado no banco de dados.      |

[//]: # (These are reference links used in the body of this note and get stripped out when the markdown processor does its job. There is no need to format nicely because it shouldn't be seen. Thanks SO - http://stackoverflow.com/questions/4823468/store-comments-in-markdown-syntax)

   [Donwload, instalar e Iniciar Neo4j Server]: <http://neo4j.com/download>
   [http://localhost:7474]: <http://localhost:7474>
