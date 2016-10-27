# encoding: UTF-8
# This file is auto-generated from the current state of the database. Instead
# of editing this file, please use the migrations feature of Active Record to
# incrementally modify your database, and then regenerate this schema definition.
#
# Note that this schema.rb definition is the authoritative source for your
# database schema. If you need to create the application database on another
# system, you should be using db:schema:load, not running all the migrations
# from scratch. The latter is a flawed and unsustainable approach (the more migrations
# you'll amass, the slower it'll run and the greater likelihood for issues).
#
# It's strongly recommended that you check this file into your version control system.

ActiveRecord::Schema.define(version: 20161027041605) do

  create_table "requests", force: :cascade do |t|
    t.string   "title"
    t.integer  "user_id"
    t.float    "amount"
    t.float    "lat"
    t.float    "long"
    t.datetime "due"
    t.text     "description"
    t.datetime "created_at",  null: false
    t.datetime "updated_at",  null: false
    t.integer  "status"
  end

  add_index "requests", ["user_id"], name: "index_requests_on_user_id"

  create_table "reviews", force: :cascade do |t|
    t.integer  "reviewer_id"
    t.integer  "reviewee_id"
    t.integer  "request_id"
    t.integer  "rating"
    t.datetime "created_at",  null: false
    t.datetime "updated_at",  null: false
  end

  add_index "reviews", ["request_id"], name: "index_reviews_on_request_id"
  add_index "reviews", ["reviewee_id"], name: "index_reviews_on_reviewee_id"
  add_index "reviews", ["reviewer_id"], name: "index_reviews_on_reviewer_id"

  create_table "transactions", force: :cascade do |t|
    t.integer  "payer_id"
    t.integer  "payee_id"
    t.float    "amount"
    t.datetime "created_at", null: false
    t.datetime "updated_at", null: false
    t.integer  "request_id"
  end

  add_index "transactions", ["payee_id"], name: "index_transactions_on_payee_id"
  add_index "transactions", ["payer_id"], name: "index_transactions_on_payer_id"
  add_index "transactions", ["request_id"], name: "index_transactions_on_request_id"

  create_table "users", force: :cascade do |t|
    t.string   "name"
    t.string   "email"
    t.float    "wallet"
    t.datetime "created_at", null: false
    t.datetime "updated_at", null: false
  end

  add_index "users", ["email"], name: "index_users_on_email"
  add_index "users", ["name"], name: "index_users_on_name"

end
