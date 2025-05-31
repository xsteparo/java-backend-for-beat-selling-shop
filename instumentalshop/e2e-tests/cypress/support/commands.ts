declare namespace Cypress {
    interface Chainable {
        loginAs(role: 'customer' | 'admin'): Chainable<void>
    }
}

Cypress.Commands.add('loginAs', (role: 'customer' | 'admin') => {
    const creds = {
        customer: { username: 'customer1', password: 'Test1234' },
        admin: { username: 'admin1', password: 'Test1234' },
    }[role]

    cy.request('POST', 'http://localhost:8080/api/v1/auth/login', creds)
        .then((resp) => {
            window.localStorage.setItem('beatshop_jwt', resp.body.token)
        })
})
