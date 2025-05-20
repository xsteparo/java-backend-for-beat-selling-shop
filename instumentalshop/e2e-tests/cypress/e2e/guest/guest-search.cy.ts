
describe('Guest track search', () => {
    it('should return matching track by name', () => {
        cy.visit('/tracks')

        // Enter search text and submit
        cy.get('input[placeholder="Search…"]').type('Beat #1')
        cy.contains('Go').click()

        // Verify the result appears
        cy.contains('Beat #1', { timeout: 10000 }).should('exist')
    })
})
