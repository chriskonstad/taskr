class CreateTransactions < ActiveRecord::Migration
  def change
    create_table :transactions do |t|
      t.references :payer, index: true, foreign_key: true
      t.references :payee, index: true, foreign_key: true
      t.float :amount

      t.timestamps null: false
    end
  end
end
