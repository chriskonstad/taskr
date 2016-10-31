class CreateRequests < ActiveRecord::Migration
  def change
    create_table :requests do |t|
      t.string :title
      t.references :user, index: true, foreign_key: true
      t.float :amount
      t.float :lat
      t.float :longitude
      t.datetime :due
      t.text :description
      #t.references :trans, index: true, foreign_key: true

      t.timestamps null: false
    end

    change_table :transactions do |t|
      t.references :request, index: true, unique: true, foreign_key: true
    end
  end
end
