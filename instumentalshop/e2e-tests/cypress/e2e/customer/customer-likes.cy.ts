describe('Customer likes a track', () => {
    beforeEach(() => {
        cy.loginAs('customer')
        cy.visit('/tracks')
    })

    it('can like and unlike a track', () => {
        // Get first like button
        cy.get('[data-testid="like-button"]').first().as('likeBtn')

        // Initially should be gray (not liked)
        cy.get('@likeBtn').find('svg').should('have.class', 'text-gray-400')

        // Like it — should become red
        cy.get('@likeBtn').click()
        cy.get('@likeBtn').find('svg').should('have.class', 'text-red-500')

        // Unlike it — should return to gray
        cy.get('@likeBtn').click()
        cy.get('@likeBtn').find('svg').should('have.class', 'text-gray-400')
    })
})
