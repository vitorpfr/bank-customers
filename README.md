# bank-customers

A bank that uses Datomic as a database to store customers.

## Usage

Run database (in datomic folder with bank-customers db created on port 4334):
`bin/transactor config/dev-transactor-template.properties`

Run app locally: 
`lein run`

(a local 'bank-customers' datomic transactor must be running on port 4334)

## Available endpoints

- Get tax-id of customers in database: 
`curl -X GET http://localhost:4000/customers | jq`

- Get data from a specific customer tax-id: 
`curl -X GET http://localhost:4000/customer?tax-id=12345678910 | jq`

## License

Copyright © 2020 FIXME

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
