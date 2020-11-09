# bank-customers

A bank that uses Datomic as a database to store customers.

## Usage

lein run

## Available endpoints

- Get tax-id of customers in database: 
curl -X GET http://localhost:4000/customers | jq

- Get data from a specific customer tax-id: 
curl -X GET http://localhost:4000/customer?tax-id=12345678910 | jq

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
