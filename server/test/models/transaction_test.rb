require 'test_helper'

class TransactionTest < ActiveSupport::TestCase
  test "simple payment" do
    payer = users(:namey)
    payee = users(:testuser)
    req = requests(:sampleopen)

    transaction = Transaction.create(payer: payer, payee: payee, amount: 100, request: req)

    assert (transaction.payer.email.equal? payer.email)
    assert (transaction.payee.email.equal? payee.email)
    assert payer.paid.include?(transaction)
    assert payee.earned.include?(transaction)
    assert_not payer.earned.include?(transaction)
    assert_not payee.paid.include?(transaction)
    assert_equal req.id, transaction.request.id
  end
end
