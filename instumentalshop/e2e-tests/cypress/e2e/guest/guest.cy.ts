describe('Guest user on /tracks', () => {
    it('should not see like or buy buttons', () => {
        cy.visit('/tracks')

        cy.contains('All beats', { timeout: 10000 }).should('be.visible')

        cy.get('[data-testid="like-button"]').should('not.exist')

        cy.get('[data-testid="buy-button"]').should('not.exist')
    })
})
