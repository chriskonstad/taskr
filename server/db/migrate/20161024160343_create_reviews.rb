class CreateReviews < ActiveRecord::Migration
  def change
    create_table :reviews do |t|
      t.references :reviewer, index: true, foreign_key: true
      t.references :reviewee, index: true, foreign_key: true
      t.references :request, index: true, foreign_key: true
      t.integer :rating

      t.timestamps null: false
    end
  end
end
