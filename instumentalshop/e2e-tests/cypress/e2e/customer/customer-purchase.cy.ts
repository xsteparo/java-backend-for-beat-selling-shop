describe('Customer completes purchase', () => {
    beforeEach(() => {
        cy.loginAs('customer')
        cy.visit('/tracks')
    })

    it('adds track to cart and successfully pays', () => {
        // Open license modal
        cy.get('[data-testid="buy-button"]').first().click()

        // Click on first license card
        cy.get('[data-testid^="license-option-"]').first().click()

        // Add to cart
        cy.get('[data-testid="add-to-cart"]').first().click()

        // Open cart panel
        cy.get('[data-testid="open-cart"]').click()

        // Confirm payment
        cy.contains('Zaplatit').click()

        // Check for success message
        cy.contains('Platba proběhla úspěšně').should('exist')
    })
})
