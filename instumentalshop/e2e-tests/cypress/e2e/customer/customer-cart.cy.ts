
describe('Customer adds track to cart', () => {
    beforeEach(() => {
        cy.loginAs('customer')
        cy.visit('/tracks')
    })

    it('can open license modal and add to cart', () => {
        // Open license modal
        cy.get('[data-testid="buy-button"]').first().click()

        // Select first license
        cy.get('[data-testid^="license-option-"]').first().click()

        // Confirm and add to cart
        cy.get('[data-testid="add-to-cart"]').first().click()

        // Open cart panel
        cy.get('[data-testid="open-cart"]').click()

        // Check cart contents
        cy.contains('Souhrn košíku').should('be.visible')
        cy.contains('Licence').should('exist')
    })
})
