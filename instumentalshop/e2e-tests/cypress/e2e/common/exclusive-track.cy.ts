// cypress/e2e/common/exclusive-track.cy.ts

describe('Exclusive license hides track', () => {
    before(() => {
        cy.loginAs('customer')
        cy.visit('/tracks')

        // Buy exclusive license
        cy.get('[data-testid="buy-button"]').first().click()
        cy.contains('Exkluzivní').click()
        cy.get('[data-testid="add-to-cart"]').eq(2).click()
        // cy.get('[data-testid="open-cart"]').click()
        cy.contains('Zaplatit').click()
    })

    it('should not show exclusive track after purchase', () => {
        cy.visit('/tracks')

        // Track should be gone
        cy.contains('Exkluzivní').should('not.exist')
    })
})
